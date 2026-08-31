package com.swahilib.feature.quiz.utils

import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.QuizAnswer
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizResult
import com.swahilib.core.games.model.QuizSet
import kotlinx.serialization.Serializable

sealed interface QuizUiState {
    data object Loading : QuizUiState
    data object Empty : QuizUiState

    data class Setup(
        val previousPoints: Int,
        val difficulty: Difficulty,
        val questionCount: Int,
    ) : QuizUiState

    data class Playing(
        val quizSet: QuizSet,
        val index: Int,
        val answers: List<QuizAnswer>,
        val previousPoints: Int,
        val livePoints: Int,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val practice: Boolean = false,
        val paused: Boolean = false,
        val answered: Boolean = false,
    ) : QuizUiState {
        val question: QuizQuestion get() = quizSet.questions[index]
        val progressLabel: String get() = "Swali ${index + 1}/${quizSet.questions.size}"
    }
    data class Finished(
        val result: QuizResult,
        val quizSet: QuizSet,
        val answers: List<QuizAnswer>,
        val activityAward: AwardResult?,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val pointsEarned: Int,
        val practice: Boolean = false,
    ) : QuizUiState
}

@Serializable
data class QuizSnapshot(val answers: List<QuizAnswer>)
