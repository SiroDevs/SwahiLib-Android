package com.swahilib.core.games.model

enum class WordSearchTheme { RANDOM, ANIMALS, FOOD, FAMILY, NATURE, VERBS, NUMBERS }

enum class WordSearchDirection(val dRow: Int, val dCol: Int) {
    HORIZONTAL(0, 1),
    VERTICAL(1, 0),
    DIAGONAL_DOWN(1, 1),
    DIAGONAL_UP(-1, 1),
}

data class PlacedWord(
    val word: String,
    val row: Int,
    val col: Int,
    val direction: WordSearchDirection,
    val clue: String,
    val found: Boolean = false,
)

data class WordSearchPuzzle(
    val size: Int,
    val grid: List<List<Char>>,
    val words: List<PlacedWord>,
)

data class WordSearchResult(
    val totalWords: Int,
    val foundWords: Int,
    val isPerfect: Boolean,
    val xpEarned: Int,
    val secondsSpent: Int,
)
