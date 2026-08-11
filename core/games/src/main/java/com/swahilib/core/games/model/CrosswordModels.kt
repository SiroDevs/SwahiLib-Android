package com.swahilib.core.games.model

enum class CrosswordDirection(val dRow: Int, val dCol: Int) {
    ACROSS(0, 1),
    DOWN(1, 0),
    DIAGONAL_DOWN_RIGHT(1, 1),
    DIAGONAL_DOWN_LEFT(1, -1),
}

data class CrosswordEntry(
    val id: String,
    val number: Int,
    val row: Int,
    val col: Int,
    val direction: CrosswordDirection,
    val answer: String,
    val clue: String,
)

data class CrosswordCell(
    val row: Int,
    val col: Int,
    val letter: Char,
    val number: Int? = null,
)

data class CrosswordPuzzle(
    val rows: Int,
    val cols: Int,
    val cells: Map<Pair<Int, Int>, CrosswordCell>,
    val entries: List<CrosswordEntry>,
)

data class CrosswordResult(
    val totalEntries: Int,
    val correctEntries: Int,
    val isPerfect: Boolean,
    val xpEarned: Int,
    val secondsSpent: Int,
)
