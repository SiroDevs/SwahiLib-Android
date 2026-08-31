package com.swahilib.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.games.EngagementRepo
import com.swahilib.core.data.repos.games.GameProgressRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
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
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
import com.swahilib.feature.quiz.utils.QuizSnapshot
import com.swahilib.feature.quiz.utils.QuizUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.random.Random

enum class QuizContentSource { WORDS, PROVERBS }

private const val QUIZ_STEP_SECONDS = 60
private const val QUIZ_POINTS_PER_CORRECT = 10

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizGenerator: QuizGenerator,
    private val proverbQuizGenerator: ProverbQuizGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    val soundPlayer: GameSoundPlayer,
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

    private val stepTimer =
        GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onStepExpired)

    private val gameType: String
        get() = (if (source == QuizContentSource.PROVERBS) StatisticsEngine.EventType.PROVERB else StatisticsEngine.EventType.QUIZ).name

    fun start(
        challengeId: String?,
        activityId: String?,
        difficulty: Difficulty = Difficulty.BEGINNER,
        questionCount: Int = 10,
        source: QuizContentSource = QuizContentSource.WORDS,
    ) {
        if (_uiState.value !is QuizUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.source = source
        this.difficulty = difficulty
        this.questionCount = questionCount

        viewModelScope.launch {
            if (challengeId != null) {
                startedAtMs = System.currentTimeMillis()
                beginSession(resume = false, practice = false)
                return@launch
            }
            val eventType =
                if (source == QuizContentSource.PROVERBS) StatisticsEngine.EventType.PROVERB else StatisticsEngine.EventType.QUIZ
            val recommended = engageRepo.recommendedDifficulty(eventType)
            val progress = gameProgressRepo.getProgress(gameType)
            _uiState.value = QuizUiState.Setup(
                previousPoints = progress.totalPoints.toInt(),
                difficulty = recommended,
                questionCount = questionCount,
            )
        }
    }

    fun updateSetupDifficulty(newDifficulty: Difficulty) {
        val state = _uiState.value as? QuizUiState.Setup ?: return
        _uiState.value = state.copy(difficulty = newDifficulty)
    }

    fun updateSetupCount(delta: Int) {
        val state = _uiState.value as? QuizUiState.Setup ?: return
        val newCount = (state.questionCount + delta).coerceIn(3, 50)
        _uiState.value = state.copy(questionCount = newCount)
    }

    fun confirmSetup(practice: Boolean) {
        val state = _uiState.value as? QuizUiState.Setup ?: return
        this.difficulty = state.difficulty
        this.questionCount =
            if (practice) 3.coerceAtMost(state.questionCount) else state.questionCount
        startedAtMs = System.currentTimeMillis()
        _uiState.value = QuizUiState.Loading
        viewModelScope.launch {
            beginSession(resume = !practice, practice = practice)
        }
    }

    private suspend fun beginSession(resume: Boolean, practice: Boolean) {
        val progress = gameProgressRepo.getProgress(gameType)
        val saved = if (resume) gameProgressRepo.loadSession(gameType) else null

        contentSeed = saved?.contentSeed ?: Random.nextLong()
        val set = when (source) {
            QuizContentSource.WORDS -> quizGenerator.generate(
                difficulty = difficulty,
                questionCount = questionCount,
                seed = contentSeed
            )

            QuizContentSource.PROVERBS -> proverbQuizGenerator.generate(
                difficulty = difficulty,
                questionCount = questionCount,
                seed = contentSeed
            )
        }
        if (set.questions.isEmpty()) {
            _uiState.value = QuizUiState.Empty
            return
        }

        var index = 0
        var answers = emptyList<QuizAnswer>()
        var livePoints = 0
        if (saved != null) {
            val snapshot =
                runCatching { Json.decodeFromString<QuizSnapshot>(saved.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                answers = snapshot.answers
                index = saved.stepIndex.coerceIn(0, set.questions.lastIndex)
                livePoints = saved.livePoints
            }
        }

        soundPlayer.startMusic()
        _uiState.value = QuizUiState.Playing(
            quizSet = set,
            index = index,
            answers = answers,
            previousPoints = progress.totalPoints.toInt(),
            livePoints = livePoints,
            secondsRemaining = QUIZ_STEP_SECONDS,
            secondsTotal = QUIZ_STEP_SECONDS,
            practice = practice,
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
        if (state.answered) return
        soundPlayer.play(GameSound.TIME_UP)
        val answer = QuizAnswer(questionId = state.question.id, correct = false)
        val updated = state.copy(answers = state.answers + answer)
        _uiState.value = updated
        if (!state.practice) persistSnapshot(updated)
        advanceStep()
    }

    fun submitChoice(optionId: String) =
        submit { question -> QuizScorer.checkChoice(question, optionId) }

    fun submitTyped(text: String) = submit { question -> QuizScorer.checkTyped(question, text) }
    fun submitMatches(connectedPairs: Map<String, String>) =
        submit { question -> QuizScorer.checkMatches(question, connectedPairs) }

    private inline fun submit(judge: (QuizQuestion) -> QuizAnswer) {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        if (state.answered || state.paused) return
        soundPlayer.play(GameSound.SUBMIT)
        stepTimer.stop()
        val answer = judge(state.question)
        val newLivePoints =
            if (answer.correct) state.livePoints + QUIZ_POINTS_PER_CORRECT else state.livePoints
        val updated = state.copy(
            answers = state.answers + answer,
            livePoints = newLivePoints,
            answered = true
        )
        _uiState.value = updated
        if (!state.practice) persistSnapshot(updated)
    }

    fun continueToNext() {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        if (!state.answered || state.paused) return
        advanceStep()
    }

    fun togglePause() {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        if (state.paused) {
            _uiState.value = state.copy(paused = false)
            stepTimer.start(state.secondsRemaining)
        } else {
            stepTimer.stop()
            _uiState.value = state.copy(paused = true)
        }
    }

    private fun advanceStep() {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        val nextIndex = state.index + 1
        if (nextIndex >= state.quizSet.questions.size) {
            finish(state)
        } else {
            _uiState.value = state.copy(
                index = nextIndex,
                secondsRemaining = QUIZ_STEP_SECONDS,
                answered = false
            )
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
                snapshotJson = Json.encodeToString(
                    QuizSnapshot.serializer(),
                    QuizSnapshot(state.answers)
                ),
            )
        }
    }

    fun restart() {
        val practice = (_uiState.value as? QuizUiState.Playing)?.practice ?: false
        stepTimer.stop()
        _uiState.value = QuizUiState.Loading
        viewModelScope.launch {
            if (!practice) gameProgressRepo.clearSession(gameType)
            beginSession(resume = false, practice = practice)
        }
    }

    fun discardAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        viewModelScope.launch {
            gameProgressRepo.clearSession(gameType)
            onDone()
        }
    }

    fun saveAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val state = _uiState.value as? QuizUiState.Playing
        if (state == null || state.practice) {
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
                snapshotJson = Json.encodeToString(
                    QuizSnapshot.serializer(),
                    QuizSnapshot(state.answers)
                ),
            )
            onDone()
        }
    }

    private fun finish(state: QuizUiState.Playing) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val answers = state.answers
        val secondsSpent =
            ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = QuizScorer.tally(answers, difficulty, secondsSpent)
        val activityType =
            if (source == QuizContentSource.PROVERBS) ActivityType.PROVERB_CHALLENGE else ActivityType.VOCABULARY_QUIZ
        val eventType =
            if (source == QuizContentSource.PROVERBS) StatisticsEngine.EventType.PROVERB else StatisticsEngine.EventType.QUIZ
        val title =
            if (source == QuizContentSource.PROVERBS) "Changamoto ya Methali" else "Jaribio la Msamiati"

        if (state.practice) {
            _uiState.value = QuizUiState.Finished(
                result = result,
                quizSet = state.quizSet,
                answers = answers,
                activityAward = null,
                unlockedAchievements = emptyList(),
                pointsEarned = 0,
                practice = true,
            )
            return
        }

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
                practice = false,
            )
        }
    }

    override fun onCleared() {
        stepTimer.stop()
        soundPlayer.stopMusic()
        super.onCleared()
    }
}
