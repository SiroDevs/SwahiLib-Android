package com.swahilib.core.games.model

import kotlinx.serialization.Serializable

data class ScrambledWord(
    val id: String,
    val answer: String,
    val scrambledLetters: List<Char>,
    val hint: String,
    val sourceWordRid: Int,
)

@Serializable
data class WordRoundResult(
    val wordId: String,
    val correct: Boolean,
    val hintsUsed: Int,
    val secondsSpent: Int,
    val gaveUp: Boolean = false,
)

data class WordSessionResult(
    val totalWords: Int,
    val correctWords: Int,
    val totalHintsUsed: Int,
    val xpEarned: Int,
    val secondsSpent: Int,
    val isPerfect: Boolean,
)
