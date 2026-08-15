package com.swahilib.feature.sudoku.utils

import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.SudokuPuzzle
import com.swahilib.core.games.model.SudokuResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import kotlinx.serialization.Serializable

sealed interface SudokuUiState {
    data object Loading : SudokuUiState
    data object Empty : SudokuUiState

    data class Overview(val previousPoints: Int) : SudokuUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : SudokuUiState

    data class Playing(
        val puzzle: SudokuPuzzle,
        val words: List<PlacedWord>,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val selectionStart: Pair<Int, Int>? = null,
        val lastMissed: Boolean = false,
        val highlightedLetter: Char? = null,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val easyMode: Boolean,
        val practice: Boolean = false,
        val paused: Boolean = false,
    ) : SudokuUiState {
        val letterPool: List<Char> get() = words.flatMap { it.word.toList() }.distinct().sorted()
    }

    data class Finished(
        val result: SudokuResult,
        val words: List<PlacedWord>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
        val practice: Boolean = false,
    ) : SudokuUiState
}

@Serializable
data class SudokuSnapshot(val foundWords: List<String>)
