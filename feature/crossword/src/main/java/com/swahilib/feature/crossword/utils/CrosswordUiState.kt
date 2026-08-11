package com.swahilib.feature.crossword.utils

import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.games.model.CrosswordPuzzle
import com.swahilib.core.games.model.CrosswordResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import kotlinx.serialization.Serializable

sealed interface CrosswordUiState {
    data object Loading : CrosswordUiState
    data object Empty : CrosswordUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : CrosswordUiState

    data class Playing(
        val puzzle: CrosswordPuzzle,
        val level: Int?,
        val previousPoints: Int,
        val answers: Map<String, String> = emptyMap(),
        val focusedEntryId: String? = null,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val easyMode: Boolean,
    ) : CrosswordUiState {
        val letterPool: List<Char> get() = puzzle.entries.flatMap { it.answer.toList() }.distinct().sorted()
    }

    data class Finished(
        val result: CrosswordResult,
        val puzzle: CrosswordPuzzle,
        val answers: Map<String, String>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
    ) : CrosswordUiState
}

@Serializable
data class CrosswordSnapshot(val answers: Map<String, String>)
