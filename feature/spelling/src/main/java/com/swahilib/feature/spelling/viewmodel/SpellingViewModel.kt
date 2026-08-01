package com.swahilib.feature.spelling.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.SpellingScorer
import com.swahilib.core.games.generator.SpellingGenerator
import com.swahilib.core.games.model.SpellingQuestion
import com.swahilib.core.games.model.SpellingResult
import com.swahilib.core.games.model.SpellingRoundResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SpellingUiState {
    data object Loading : SpellingUiState
    data object Empty : SpellingUiState
    data class Playing(
        val questions: List<SpellingQuestion>,
        val index: Int,
        val revealedLetters: Int = 0,
        val lastResult: SpellingRoundResult? = null,
    ) : SpellingUiState {
        val question: SpellingQuestion get() = questions[index]
        val hintText: String get() = question.answer.take(revealedLetters) +
            "_".repeat((question.answer.length - revealedLetters).coerceAtLeast(0))
    }
    data class Finished(val result: SpellingResult) : SpellingUiState
}

@HiltViewModel
class SpellingViewModel @Inject constructor(
    private val generator: SpellingGenerator,
    private val engagementRepo: EngagementRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SpellingUiState>(SpellingUiState.Loading)
    val uiState: StateFlow<SpellingUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L
    private val rounds = mutableListOf<SpellingRoundResult>()

    fun start(
        challengeId: String?,
        activityId: String?,
        difficulty: Difficulty = Difficulty.BEGINNER,
        questionCount: Int = 5,
    ) {
        if (_uiState.value !is SpellingUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            val questions = generator.generate(difficulty, questionCount)
            _uiState.value = if (questions.isEmpty()) SpellingUiState.Empty else SpellingUiState.Playing(questions, index = 0)
        }
    }

    fun useHint() {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        if (state.lastResult != null || state.revealedLetters >= state.question.answer.length - 1) return
        _uiState.value = state.copy(revealedLetters = state.revealedLetters + 1)
    }

    fun submit(typed: String) {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        if (state.lastResult != null) return
        val result = SpellingScorer.checkAnswer(state.question, typed, state.revealedLetters)
        rounds.add(result)
        _uiState.value = state.copy(lastResult = result)
    }

    fun next() {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        if (state.lastResult == null) return
        val nextIndex = state.index + 1
        if (nextIndex >= state.questions.size) {
            finish()
        } else {
            _uiState.value = state.copy(index = nextIndex, revealedLetters = 0, lastResult = null)
        }
    }

    private fun finish() {
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = SpellingScorer.tally(rounds, difficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engagementRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.SPELLING_CHALLENGE, difficulty)
            } else if (result.xpEarned > 0) {
                engagementRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.SPELLING_CHALLENGE,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            engagementRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.SPELLING,
                title = "Changamoto ya Tahajia",
                score = result.fullyCorrectCount,
                maxScore = result.totalQuestions,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            _uiState.value = SpellingUiState.Finished(result.copy(xpEarned = xpEarnedThisSession))
        }
    }
}
