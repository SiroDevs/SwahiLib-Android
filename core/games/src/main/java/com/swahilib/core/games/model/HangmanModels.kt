package com.swahilib.core.games.model

data class HangmanRound(
    val answer: String,
    val hint: String,
    val sourceWordRid: Int,
    val guessedLetters: Set<Char> = emptySet(),
    val wrongGuesses: Int = 0,
) {
    val maxWrongGuesses = 6
    val displayWord: String get() = answer.map { if (it in guessedLetters || it == ' ') it else '_' }.joinToString(" ")
    val isWon: Boolean get() = answer.all { it == ' ' || it in guessedLetters }
    val isLost: Boolean get() = wrongGuesses >= maxWrongGuesses
    val isOver: Boolean get() = isWon || isLost
}

data class HangmanSessionResult(
    val totalWords: Int,
    val wonWords: Int,
    val xpEarned: Int,
    val secondsSpent: Int,
    val isPerfect: Boolean,
)
