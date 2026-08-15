package com.swahilib.feature.spelling.utils

import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.games.model.SpellingQuestion
import com.swahilib.core.games.model.SpellingResult
import com.swahilib.core.games.model.SpellingRoundResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import kotlinx.serialization.Serializable

sealed interface SpellingUiState {
    data object Loading : SpellingUiState
    data object Empty : SpellingUiState

    data class Overview(val previousPoints: Int) : SpellingUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : SpellingUiState

    data class Playing(
        val questions: List<SpellingQuestion>,
        val index: Int,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val revealedLetters: Int = 0,
        val locked: Boolean = false,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val practice: Boolean = false,
        val paused: Boolean = false,
    ) : SpellingUiState {
        val question: SpellingQuestion get() = questions[index]
        val hintText: String get() = question.answer.take(revealedLetters) +
            "_".repeat((question.answer.length - revealedLetters).coerceAtLeast(0))
    }

    data class Finished(
        val result: SpellingResult,
        val questions: List<SpellingQuestion>,
        val rounds: List<SpellingRoundResult>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
        val practice: Boolean = false,
    ) : SpellingUiState
}

@Serializable
data class SpellingSnapshot(val roundsSoFar: List<SpellingRoundResult>)
