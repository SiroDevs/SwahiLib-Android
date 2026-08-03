package com.swahilib.core.games.model

data class SpellingQuestion(
    val id: String,
    val clue: String,
    val answer: String,
    val sourceRid: Int,
)

data class SpellingRoundResult(
    val questionId: String,
    val typed: String,
    val fullyCorrect: Boolean,
    val partialCredit: Float, // 0f..1f, similarity ratio for near-misses
    val hintsUsed: Int,
)

data class SpellingResult(
    val totalQuestions: Int,
    val fullyCorrectCount: Int,
    val averageCredit: Float,
    val isPerfect: Boolean,
    val xpEarned: Int,
    val secondsSpent: Int,
)
