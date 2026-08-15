package com.swahilib.feature.hangman.utils

import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.games.model.HangmanRound
import com.swahilib.core.games.model.HangmanSessionResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import kotlinx.serialization.Serializable

sealed interface HangmanUiState {
    data object Loading : HangmanUiState
    data object Empty : HangmanUiState

    /** "Here's what awaits you" briefing shown before the level carousel, with a practice option. */
    data class Overview(val previousPoints: Int) : HangmanUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : HangmanUiState

    data class Playing(
        val rounds: List<HangmanRound>,
        val index: Int,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val justAdvanced: Boolean = false,
        val practice: Boolean = false,
        val paused: Boolean = false,
    ) : HangmanUiState {
        val round: HangmanRound get() = rounds[index]
    }

    data class Finished(
        val result: HangmanSessionResult,
        val rounds: List<HangmanRound>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
        val practice: Boolean = false,
    ) : HangmanUiState
}

@Serializable
data class HangmanRoundSnapshot(
    val guessedLetters: String,
    val wrongGuesses: Int,
)

@Serializable
data class HangmanSnapshot(val roundsSoFar: List<HangmanRoundSnapshot>)
