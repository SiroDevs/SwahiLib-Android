package com.swahilib.feature.word_builder.utils

import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.games.model.ScrambledWord
import com.swahilib.core.games.model.WordRoundResult
import com.swahilib.core.games.model.WordSessionResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import kotlinx.serialization.Serializable

sealed interface WordBuilderUiState {
    data object Loading : WordBuilderUiState
    data object Empty : WordBuilderUiState

    data class Overview(val previousPoints: Int) : WordBuilderUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : WordBuilderUiState

    data class Playing(
        val word: ScrambledWord,
        val roundIndex: Int,
        val totalRounds: Int,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val pickedIndices: List<Int> = emptyList(),
        val revealedCount: Int = 0,
        val hintsUsed: Int = 0,
        val locked: Boolean = false,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val practice: Boolean = false,
        val paused: Boolean = false,
    ) : WordBuilderUiState {
        val assembled: String get() = pickedIndices.joinToString("") { word.scrambledLetters[it].toString() }
    }

    data class Finished(
        val result: WordSessionResult,
        val rounds: List<Pair<ScrambledWord, WordRoundResult>>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
        val practice: Boolean = false,
    ) : WordBuilderUiState
}

@Serializable
data class WordBuilderSnapshot(val roundsSoFar: List<WordRoundResult>)
