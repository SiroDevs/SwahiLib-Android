package com.swahilib.core.games.model

/**
 * A placement direction as a (row, col) step vector, so the generator and
 * renderer can treat every direction - including diagonals - uniformly
 * instead of branching on ACROSS/DOWN everywhere.
 */
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

/** A single grid cell. Null means "not part of the puzzle" (a black square). */
data class CrosswordCell(
    val row: Int,
    val col: Int,
    val letter: Char,
    val number: Int? = null, // set only on cells that start an entry
)

data class CrosswordPuzzle(
    val rows: Int,
    val cols: Int,
    val cells: Map<Pair<Int, Int>, CrosswordCell>, // sparse - absent = black square
    val entries: List<CrosswordEntry>,
)

data class CrosswordResult(
    val totalEntries: Int,
    val correctEntries: Int,
    val isPerfect: Boolean,
    val xpEarned: Int,
    val secondsSpent: Int,
)
