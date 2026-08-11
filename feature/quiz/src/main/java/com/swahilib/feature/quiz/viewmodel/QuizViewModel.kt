package com.swahilib.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.data.repos.GameProgressRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.GameStepTimer
import com.swahilib.core.games.engine.QuizScorer
import com.swahilib.core.games.generator.ProverbQuizGenerator
import com.swahilib.core.games.generator.QuizGenerator
import com.swahilib.core.games.model.QuizAnswer
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizResult
import com.swahilib.core.games.model.QuizSet
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.random.Random

/** Which vocabulary source this quiz session pulls from - drives content, XP activity type, and stats event. */
enum class QuizContentSource { WORDS, PROVERBS }

/** Quiz has no kiwango levels - a flat per-question timer and a simple points bucket per content source. */
private const val QUIZ_STEP_SECONDS = 60
private const val QUIZ_POINTS_PER_CORRECT = 10

sealed interface QuizUiState {
    data object Loading : QuizUiState
    data object Empty : QuizUiState
    data class Playing(
        val quizSet: QuizSet,
        val index: Int,
        val answers: List<QuizAnswer>,
        val previousPoints: Int,
        val livePoints: Int,
        val secondsRemaining: Int,
        val secondsTotal: Int,
    ) : QuizUiState {
        val question: QuizQuestion get() = quizSet.questions[index]
        val progressLabel: String get() = "Swali ${index + 1}/${quizSet.questions.size}"
    }
    data class Finished(
        val result: QuizResult,
        val quizSet: QuizSet,
        val answers: List<QuizAnswer>,
        val activityAward: AwardResult?,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val pointsEarned: Int,
    ) : QuizUiState
}

@Serializable
private data class QuizSnapshot(val answers: List<QuizAnswer>)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizGenerator: QuizGenerator,
    private val proverbQuizGenerator: ProverbQuizGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    private val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var source: QuizContentSource = QuizContentSource.WORDS
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L
    private var questionCount: Int = 5

    private val stepTimer = GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onStepExpired)

    private val gameType: String
        get() = (if (source == QuizContentSource.PROVERBS) StatisticsEngine.EventType.PROVERB else StatisticsEngine.EventType.QUIZ).name

    /** Call once when the screen is first composed. Safe to call repeatedly - only the first call starts a session. */
    fun start(
        challengeId: String?,
        activityId: String?,
        difficulty: Difficulty = Difficulty.BEGINNER,
        questionCount: Int = 5,
        source: QuizContentSource = QuizContentSource.WORDS,
    ) {
        if (_uiState.value !is QuizUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.source = source
        this.difficulty = difficulty
        this.questionCount = questionCount
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            this@QuizViewModel.difficulty = if (challengeId == null) {
                val eventType = if (source == QuizContentSource.PROVERBS) StatisticsEngine.EventType.PROVERB else StatisticsEngine.EventType.QUIZ
                engageRepo.recommendedDifficulty(eventType)
            } else {
                difficulty
            }
            beginSession(resume = challengeId == null)
        }
    }

    private suspend fun beginSession(resume: Boolean) {
        val progress = gameProgressRepo.getProgress(gameType)
        val saved = if (resume) gameProgressRepo.loadSession(gameType) else null

        contentSeed = saved?.contentSeed ?: Random.nextLong()
        val set = when (source) {
            QuizContentSource.WORDS -> quizGenerator.generate(difficulty = difficulty, questionCount = questionCount, seed = contentSeed)
            QuizContentSource.PROVERBS -> proverbQuizGenerator.generate(difficulty = difficulty, questionCount = questionCount, seed = contentSeed)
        }
        if (set.questions.isEmpty()) {
            _uiState.value = QuizUiState.Empty
            return
        }

        var index = 0
        var answers = emptyList<QuizAnswer>()
        var livePoints = 0
        if (saved != null) {
            val snapshot = runCatching { Json.decodeFromString<QuizSnapshot>(saved.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                answers = snapshot.answers
                index = saved.stepIndex.coerceIn(0, set.questions.lastIndex)
                livePoints = saved.livePoints
            }
        }

        _uiState.value = QuizUiState.Playing(
            quizSet = set,
            index = index,
            answers = answers,
            previousPoints = progress.totalPoints.toInt(),
            livePoints = livePoints,
            secondsRemaining = QUIZ_STEP_SECONDS,
            secondsTotal = QUIZ_STEP_SECONDS,
        )
        stepTimer.start(QUIZ_STEP_SECONDS)
    }

    private fun onTick(secondsRemaining: Int) {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onStepExpired() {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        soundPlayer.play(GameSound.TIME_UP)
        val answer = QuizAnswer(questionId = state.question.id, correct = false)
        recordAnswerAndAdvance(state, answer)
    }

    fun submitChoice(optionId: String) = submit { question -> QuizScorer.checkChoice(question, optionId) }
    fun submitTyped(text: String) = submit { question -> QuizScorer.checkTyped(question, text) }
    fun submitMatches(connectedPairs: Map<String, String>) = submit { question -> QuizScorer.checkMatches(question, connectedPairs) }

    private inline fun submit(judge: (QuizQuestion) -> QuizAnswer) {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        soundPlayer.play(GameSound.SUBMIT)
        val answer = judge(state.question)
        recordAnswerAndAdvance(state, answer)
    }

    private fun recordAnswerAndAdvance(state: QuizUiState.Playing, answer: QuizAnswer) {
        stepTimer.stop()
        val newLivePoints = if (answer.correct) state.livePoints + QUIZ_POINTS_PER_CORRECT else state.livePoints
        val updated = state.copy(answers = state.answers + answer, livePoints = newLivePoints)
        _uiState.value = updated
        persistSnapshot(updated)
        viewModelScope.launch {
            delay(450)
            advanceStep()
        }
    }

    private fun advanceStep() {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        val nextIndex = state.index + 1
        if (nextIndex >= state.quizSet.questions.size) {
            finish(state)
        } else {
            _uiState.value = state.copy(index = nextIndex, secondsRemaining = QUIZ_STEP_SECONDS)
            stepTimer.start(QUIZ_STEP_SECONDS)
        }
    }

    private fun persistSnapshot(state: QuizUiState.Playing) {
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = 0,
                contentSeed = contentSeed,
                stepIndex = state.index,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(QuizSnapshot.serializer(), QuizSnapshot(state.answers)),
            )
        }
    }

    /** Refresh action, after "Ndio": brand-new question set, same source/difficulty. */
    fun restart() {
        stepTimer.stop()
        _uiState.value = QuizUiState.Loading
        viewModelScope.launch {
            gameProgressRepo.clearSession(gameType)
            beginSession(resume = false)
        }
    }

    fun discardAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        viewModelScope.launch {
            gameProgressRepo.clearSession(gameType)
            onDone()
        }
    }

    fun saveAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        val state = _uiState.value as? QuizUiState.Playing
        if (state == null) {
            onDone()
            return
        }
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = 0,
                contentSeed = contentSeed,
                stepIndex = state.index,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(QuizSnapshot.serializer(), QuizSnapshot(state.answers)),
            )
            onDone()
        }
    }

    private fun finish(state: QuizUiState.Playing) {
        stepTimer.stop()
        val answers = state.answers
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = QuizScorer.tally(answers, difficulty, secondsSpent)
        val activityType = if (source == QuizContentSource.PROVERBS) ActivityType.PROVERB_CHALLENGE else ActivityType.VOCABULARY_QUIZ
        val eventType = if (source == QuizContentSource.PROVERBS) StatisticsEngine.EventType.PROVERB else StatisticsEngine.EventType.QUIZ
        val title = if (source == QuizContentSource.PROVERBS) "Changamoto ya Methali" else "Jaribio la Msamiati"

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var award: AwardResult? = null
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                val completion = engageRepo.markActivityComplete(cId, aId, secondsSpent)
                award = completion?.activityAward
                xpEarnedThisSession = RewardRules.activityXp(activityType, difficulty)
            } else {
                award = engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = activityType,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = eventType,
                title = title,
                score = result.correctAnswers,
                maxScore = result.totalQuestions,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            gameProgressRepo.completeLevel(gameType, level = 0, pointsEarned = state.livePoints)
            soundPlayer.play(GameSound.LEVEL_COMPLETE)

            _uiState.value = QuizUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                quizSet = state.quizSet,
                answers = answers,
                activityAward = award,
                unlockedAchievements = unlocked,
                pointsEarned = state.livePoints,
            )
        }
    }

    override fun onCleared() {
        stepTimer.stop()
        super.onCleared()
    }
}
