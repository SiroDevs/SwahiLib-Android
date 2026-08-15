package com.swahilib.feature.sentence_builder.utils

import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.games.model.SentenceQuestion
import com.swahilib.core.games.model.SentenceResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import kotlinx.serialization.Serializable

sealed interface SentenceUiState {
    data object Loading : SentenceUiState
    data object Empty : SentenceUiState

    data class Overview(val previousPoints: Int) : SentenceUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : SentenceUiState

    data class Playing(
        val questions: List<SentenceQuestion>,
        val index: Int,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val pickedIndices: List<Int> = emptyList(),
        val locked: Boolean = false,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val practice: Boolean = false,
        val paused: Boolean = false,
    ) : SentenceUiState {
        val question: SentenceQuestion get() = questions[index]
        val picked: List<String> get() = pickedIndices.map { question.shuffledWords[it] }
    }

    data class Finished(
        val result: SentenceResult,
        val questions: List<SentenceQuestion>,
        val correctness: List<Boolean>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
        val practice: Boolean = false,
    ) : SentenceUiState
}

@Serializable
data class SentenceSnapshot(val correctness: List<Boolean>)
