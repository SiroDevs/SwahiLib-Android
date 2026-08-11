package com.swahilib.core.games.model

data class SentenceQuestion(
    val id: String,
    val shuffledWords: List<String>,
    val correctOrder: List<String>,
    val explanation: String,
    val sourceRid: Int,
) {
    val correctSentence: String get() = correctOrder.joinToString(" ")
}

data class SentenceResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val isPerfect: Boolean,
    val xpEarned: Int,
    val secondsSpent: Int,
)
