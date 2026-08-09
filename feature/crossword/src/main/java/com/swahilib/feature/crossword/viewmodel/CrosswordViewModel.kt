package com.swahilib.feature.crossword.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.CrosswordScorer
import com.swahilib.core.games.generator.CrosswordGenerator
import com.swahilib.core.games.model.CrosswordPuzzle
import com.swahilib.core.games.model.CrosswordResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CrosswordUiState {
    data object Loading : CrosswordUiState
    data object Empty : CrosswordUiState
    data class Playing(
        val puzzle: CrosswordPuzzle,
        val answers: Map<String, String> = emptyMap(),
        val checked: Boolean = false,
    ) : CrosswordUiState
    data class Finished(val result: CrosswordResult, val unlockedAchievements: List<com.swahilib.core.engagement.model.Achievement> = emptyList()) : CrosswordUiState
}

@HiltViewModel
class CrosswordViewModel @Inject constructor(
    private val generator: CrosswordGenerator,
    private val engageRepo: EngagementRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CrosswordUiState>(CrosswordUiState.Loading)
    val uiState: StateFlow<CrosswordUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L

    fun start(challengeId: String?, activityId: String?, difficulty: Difficulty = Difficulty.BEGINNER) {
        if (_uiState.value !is CrosswordUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            this@CrosswordViewModel.difficulty = if (challengeId == null) {
                engageRepo.recommendedDifficulty(StatisticsEngine.EventType.CROSSWORD)
            } else {
                difficulty
            }
            val puzzle = generator.generate(this@CrosswordViewModel.difficulty)
            _uiState.value = if (puzzle.entries.size < 2) CrosswordUiState.Empty else CrosswordUiState.Playing(puzzle)
        }
    }

    fun updateAnswer(entryId: String, text: String) {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        if (state.checked) return
        _uiState.value = state.copy(answers = state.answers + (entryId to text))
    }

    /** Checks all entries at once and reveals correctness; call [finish] separately once the user is ready to bank XP. */
    fun check() {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        _uiState.value = state.copy(checked = true)
    }

    fun finish() {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = CrosswordScorer.tally(state.puzzle.entries, state.answers, difficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engageRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.CROSSWORD, difficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.CROSSWORD,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.CROSSWORD,
                title = "CrossWord",
                score = result.correctEntries,
                maxScore = result.totalEntries,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            _uiState.value = CrosswordUiState.Finished(result.copy(xpEarned = xpEarnedThisSession), unlocked)
        }
    }
}
