package com.swahilib.core.games.model

/**
 * Sprint 2 - Word Builder. User reconstructs a Swahili word from its
 * scrambled letters. Supports hints (each hint reveals the next correct
 * letter and costs XP), timed mode, and endless mode (caller just keeps
 * calling WordBuilderGenerator.next() until the user stops).
 */
data class ScrambledWord(
    val id: String,
    val answer: String,
    val scrambledLetters: List<Char>,
    val hint: String,
    val sourceWordRid: Int,
)

data class WordBuilderRoundResult(
    val wordId: String,
    val correct: Boolean,
    val hintsUsed: Int,
    val secondsSpent: Int,
    val gaveUp: Boolean = false,
)

data class WordBuilderSessionResult(
    val totalWords: Int,
    val correctWords: Int,
    val totalHintsUsed: Int,
    val xpEarned: Int,
    val secondsSpent: Int,
    val isPerfect: Boolean,
)
