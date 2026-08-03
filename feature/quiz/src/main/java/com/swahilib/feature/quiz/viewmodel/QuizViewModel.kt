package com.swahilib.feature.quiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.QuizScorer
import com.swahilib.core.games.generator.ProverbQuizGenerator
import com.swahilib.core.games.generator.QuizGenerator
import com.swahilib.core.games.model.QuizAnswer
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizResult
import com.swahilib.core.games.model.QuizSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Which vocabulary source this quiz session pulls from - drives content, XP activity type, and stats event. */
enum class QuizContentSource { WORDS, PROVERBS }

sealed interface QuizUiState {
    data object Loading : QuizUiState
    data object Empty : QuizUiState
    data class Playing(
        val quizSet: QuizSet,
        val index: Int,
        val answers: List<QuizAnswer>,
        val lastAnswer: QuizAnswer? = null,
    ) : QuizUiState {
        val question: QuizQuestion get() = quizSet.questions[index]
        val progressLabel: String get() = "Swali ${index + 1}/${quizSet.questions.size}"
    }
    data class Finished(val result: QuizResult, val activityAward: AwardResult?, val unlockedAchievements: List<Achievement> = emptyList()) : QuizUiState
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizGenerator: QuizGenerator,
    private val proverbQuizGenerator: ProverbQuizGenerator,
    private val engagementRepo: EngagementRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var source: QuizContentSource = QuizContentSource.WORDS
    private var startedAtMs: Long = 0L

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
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            // Freeplay sessions adapt to recent accuracy; challenge sessions keep the challenge's fixed difficulty.
            this@QuizViewModel.difficulty = if (challengeId == null) {
                val eventType = if (source == QuizContentSource.PROVERBS) StatisticsEngine.EventType.PROVERB else StatisticsEngine.EventType.QUIZ
                engagementRepo.recommendedDifficulty(eventType)
            } else {
                difficulty
            }

            val set = when (source) {
                QuizContentSource.WORDS -> quizGenerator.generate(difficulty = this@QuizViewModel.difficulty, questionCount = questionCount)
                QuizContentSource.PROVERBS -> proverbQuizGenerator.generate(difficulty = this@QuizViewModel.difficulty, questionCount = questionCount)
            }
            _uiState.value = if (set.questions.isEmpty()) {
                QuizUiState.Empty
            } else {
                QuizUiState.Playing(quizSet = set, index = 0, answers = emptyList())
            }
        }
    }

    fun submitChoice(optionId: String) = submit { question ->
        QuizScorer.checkChoice(question, optionId)
    }

    fun submitTyped(text: String) = submit { question ->
        QuizScorer.checkTyped(question, text)
    }

    fun submitMatches(connectedPairs: Map<String, String>) = submit { question ->
        QuizScorer.checkMatches(question, connectedPairs)
    }

    private inline fun submit(crossinline judge: (QuizQuestion) -> QuizAnswer) {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        if (state.lastAnswer != null) return // already answered this question
        val answer = judge(state.question)
        _uiState.value = state.copy(lastAnswer = answer)
    }

    /** Advances past the just-revealed answer, or finishes the session on the last question. */
    fun next() {
        val state = _uiState.value as? QuizUiState.Playing ?: return
        val lastAnswer = state.lastAnswer ?: return
        val answers = state.answers + lastAnswer
        val nextIndex = state.index + 1

        if (nextIndex >= state.quizSet.questions.size) {
            finish(answers)
        } else {
            _uiState.value = state.copy(index = nextIndex, answers = answers, lastAnswer = null)
        }
    }

    private fun finish(answers: List<QuizAnswer>) {
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
                // Challenge mode: fixed XP already tuned per-activity by RewardRules;
                // markActivityComplete awards it and handles challenge-completion bonus.
                val completion = engagementRepo.markActivityComplete(cId, aId, secondsSpent)
                award = completion?.activityAward
                xpEarnedThisSession = RewardRules.activityXp(activityType, difficulty)
            } else {
                // Freeplay: XP scales with how many questions were answered correctly.
                award = engagementRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = activityType,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engagementRepo.recordLearningEvent(
                type = eventType,
                title = title,
                score = result.correctAnswers,
                maxScore = result.totalQuestions,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            // result.xpEarned is a partial-credit *estimate*; in challenge mode the
            // real award is the fixed activity XP, so surface that on the result screen.
            _uiState.value = QuizUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                activityAward = award,
                unlockedAchievements = unlocked,
            )
        }
    }
}
