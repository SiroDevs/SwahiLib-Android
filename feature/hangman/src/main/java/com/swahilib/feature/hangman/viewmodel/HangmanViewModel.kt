package com.swahilib.feature.hangman.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.HangmanScorer
import com.swahilib.core.games.generator.HangmanGenerator
import com.swahilib.core.games.model.HangmanRound
import com.swahilib.core.games.model.HangmanSessionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HangmanUiState {
    data object Loading : HangmanUiState
    data object Empty : HangmanUiState
    data class Playing(val rounds: List<HangmanRound>, val index: Int) : HangmanUiState {
        val round: HangmanRound get() = rounds[index]
    }
    data class Finished(val result: HangmanSessionResult, val unlockedAchievements: List<Achievement> = emptyList()) : HangmanUiState
}

@HiltViewModel
class HangmanViewModel @Inject constructor(
    private val generator: HangmanGenerator,
    private val engagementRepo: EngagementRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HangmanUiState>(HangmanUiState.Loading)
    val uiState: StateFlow<HangmanUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L

    fun start(challengeId: String?, activityId: String?, difficulty: Difficulty = Difficulty.BEGINNER, wordCount: Int = 5) {
        if (_uiState.value !is HangmanUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            this@HangmanViewModel.difficulty = if (challengeId == null) {
                engagementRepo.recommendedDifficulty(StatisticsEngine.EventType.HANGMAN)
            } else {
                difficulty
            }
            val rounds = generator.session(this@HangmanViewModel.difficulty, wordCount)
            _uiState.value = if (rounds.isEmpty()) HangmanUiState.Empty else HangmanUiState.Playing(rounds, index = 0)
        }
    }

    fun guess(letter: Char) {
        val state = _uiState.value as? HangmanUiState.Playing ?: return
        if (state.round.isOver) return
        val updated = HangmanScorer.guess(state.round, letter.uppercaseChar())
        val newRounds = state.rounds.toMutableList().apply { set(state.index, updated) }
        _uiState.value = state.copy(rounds = newRounds)
    }

    fun next() {
        val state = _uiState.value as? HangmanUiState.Playing ?: return
        if (!state.round.isOver) return
        val nextIndex = state.index + 1
        if (nextIndex >= state.rounds.size) {
            finish(state.rounds)
        } else {
            _uiState.value = state.copy(index = nextIndex)
        }
    }

    private fun finish(rounds: List<HangmanRound>) {
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = HangmanScorer.tally(rounds, difficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engagementRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.HANGMAN, difficulty)
            } else if (result.xpEarned > 0) {
                engagementRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.HANGMAN,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engagementRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.HANGMAN,
                title = "Mchezo wa Hangman",
                score = result.wonWords,
                maxScore = result.totalWords,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            _uiState.value = HangmanUiState.Finished(result.copy(xpEarned = xpEarnedThisSession), unlocked)
        }
    }
}
