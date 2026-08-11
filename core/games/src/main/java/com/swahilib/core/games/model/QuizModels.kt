package com.swahilib.core.games.model

import com.swahilib.core.engagement.model.Difficulty
import kotlinx.serialization.Serializable

enum class QuizFormat {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    FILL_IN_BLANK,
    MATCH_WORDS,
}

data class QuizOption(
    val id: String,
    val text: String,
)

data class QuizQuestion(
    val id: String,
    val format: QuizFormat,
    val prompt: String,
    val options: List<QuizOption> = emptyList(),
    val correctOptionIds: Set<String> = emptySet(),
    val expectedText: String? = null,
    val matchLeft: List<QuizOption> = emptyList(),
    val matchRight: List<QuizOption> = emptyList(),
    val explanation: String,
    val sourceWordRid: Int? = null,
)

data class QuizSet(
    val id: String,
    val difficulty: Difficulty,
    val questions: List<QuizQuestion>,
)

@Serializable
data class QuizAnswer(
    val questionId: String,
    val selectedOptionIds: Set<String> = emptySet(),
    val typedText: String? = null,
    val correct: Boolean,
)

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val isPerfect: Boolean,
    val xpEarned: Int,
    val secondsSpent: Int,
)
