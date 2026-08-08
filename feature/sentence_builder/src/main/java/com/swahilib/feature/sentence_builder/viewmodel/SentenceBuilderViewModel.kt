package com.swahilib.feature.sentence_builder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.SentenceBuilderScorer
import com.swahilib.core.games.generator.SentenceBuilderGenerator
import com.swahilib.core.games.model.SentenceQuestion
import com.swahilib.core.games.model.SentenceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SentenceUiState {
    data object Loading : SentenceUiState
    data object Empty : SentenceUiState
    data class Playing(
        val questions: List<SentenceQuestion>,
        val index: Int,
        val pickedIndices: List<Int> = emptyList(),
        val feedback: Boolean? = null,
    ) : SentenceUiState {
        val question: SentenceQuestion get() = questions[index]
        val picked: List<String> get() = pickedIndices.map { question.shuffledWords[it] }
    }
    data class Finished(val result: SentenceResult, val unlockedAchievements: List<com.swahilib.core.engagement.model.Achievement> = emptyList()) : SentenceUiState
}

@HiltViewModel
class SentenceBuilderViewModel @Inject constructor(
    private val generator: SentenceBuilderGenerator,
    private val engageRepo: EngagementRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SentenceUiState>(SentenceUiState.Loading)
    val uiState: StateFlow<SentenceUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L
    private val results = mutableListOf<Boolean>()

    fun start(
        challengeId: String?,
        activityId: String?,
        difficulty: Difficulty = Difficulty.BEGINNER,
        questionCount: Int = 5,
    ) {
        if (_uiState.value !is SentenceUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            this@SentenceBuilderViewModel.difficulty = if (challengeId == null) {
                engageRepo.recommendedDifficulty(StatisticsEngine.EventType.SENTENCE_BUILDER)
            } else {
                difficulty
            }
            val questions = generator.generate(this@SentenceBuilderViewModel.difficulty, questionCount)
            _uiState.value = if (questions.isEmpty()) {
                SentenceUiState.Empty
            } else {
                SentenceUiState.Playing(questions, index = 0)
            }
        }
    }

    fun pickWord(index: Int) {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.feedback != null || index in state.pickedIndices) return
        _uiState.value = state.copy(pickedIndices = state.pickedIndices + index)
    }

    fun clear() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.feedback != null) return
        _uiState.value = state.copy(pickedIndices = emptyList())
    }

    fun submit() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.feedback != null || state.pickedIndices.size != state.question.shuffledWords.size) return
        val correct = SentenceBuilderScorer.check(state.question, state.picked)
        results.add(correct)
        _uiState.value = state.copy(feedback = correct)
    }

    fun next() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.feedback == null) return
        val nextIndex = state.index + 1
        if (nextIndex >= state.questions.size) {
            finish()
        } else {
            _uiState.value = state.copy(index = nextIndex, pickedIndices = emptyList(), feedback = null)
        }
    }

    private fun finish() {
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = SentenceBuilderScorer.tally(results, difficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engageRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.SENTENCE_BUILDER, difficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.SENTENCE_BUILDER,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.SENTENCE_BUILDER,
                title = "Panga Sentensi",
                score = result.correctAnswers,
                maxScore = result.totalQuestions,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            _uiState.value = SentenceUiState.Finished(result.copy(xpEarned = xpEarnedThisSession), unlocked)
        }
    }
}
