package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.CrosswordCell
import com.swahilib.core.games.model.CrosswordDirection
import com.swahilib.core.games.model.CrosswordEntry
import com.swahilib.core.games.model.CrosswordPuzzle
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

private data class Candidate(val answer: String, val clue: String)

/**
 * Greedy crossword builder: places the longest candidate first, then tries
 * to intersect every following candidate with an already-placed word at a
 * shared letter. Runs on an unbounded (row, col) plane during placement,
 * then the puzzle's bounding box becomes the final grid size. Clues mix
 * word definitions/English meanings (per CLAUDE.md) with single-word idioms
 * where available; proverbs/synonyms aren't gridable as single answers so
 * they're left to Proverb Challenge and Sentence Builder instead.
 */
@Singleton
class CrosswordGenerator @Inject constructor(
    private val wordDao: WordDao,
    private val idiomDao: IdiomDao,
) {

    suspend fun generate(
        difficulty: Difficulty = Difficulty.BEGINNER,
        targetEntries: Int = 7,
        seed: Long = System.currentTimeMillis(),
    ): CrosswordPuzzle {
        val random = Random(seed)
        val lengthRange = when (difficulty) {
            Difficulty.BEGINNER -> 3..7
            Difficulty.INTERMEDIATE -> 3..9
            Difficulty.ADVANCED -> 3..12
        }

        val words = wordDao.getAll().first().mapNotNull { word ->
            val answer = word.title?.trim()?.uppercase().orEmpty()
            val clue = word.meaning?.takeIf { it.isNotBlank() } ?: word.english?.takeIf { it.isNotBlank() }
            if (answer.isNotBlank() && answer.all { it.isLetter() } && answer.length in lengthRange && clue != null) {
                Candidate(answer, clue)
            } else null
        }
        val idioms = idiomDao.getAll().first().mapNotNull { idiom ->
            val answer = idiom.title?.trim()?.uppercase().orEmpty()
            val clue = idiom.meaning?.takeIf { it.isNotBlank() }
            if (answer.isNotBlank() && answer.all { it.isLetter() } && answer.length in lengthRange && clue != null) {
                Candidate(answer, "Nahau: $clue")
            } else null
        }

        val candidates = (words + idioms).distinctBy { it.answer }.shuffled(random)
            .sortedByDescending { it.answer.length }

        if (candidates.isEmpty()) {
            return CrosswordPuzzle(rows = 0, cols = 0, cells = emptyMap(), entries = emptyList())
        }

        return place(candidates, targetEntries)
    }

    private fun place(candidates: List<Candidate>, targetEntries: Int): CrosswordPuzzle {
        data class Placed(val candidate: Candidate, val row: Int, val col: Int, val direction: CrosswordDirection)

        val placed = mutableListOf<Placed>()
        val plane = mutableMapOf<Pair<Int, Int>, Char>()

        fun fits(candidate: Candidate, row: Int, col: Int, direction: CrosswordDirection): Boolean {
            for (i in candidate.answer.indices) {
                val r = if (direction == CrosswordDirection.DOWN) row + i else row
                val c = if (direction == CrosswordDirection.ACROSS) col + i else col
                val existing = plane[r to c]
                if (existing != null && existing != candidate.answer[i]) return false
            }
            return true
        }

        fun commit(candidate: Candidate, row: Int, col: Int, direction: CrosswordDirection) {
            for (i in candidate.answer.indices) {
                val r = if (direction == CrosswordDirection.DOWN) row + i else row
                val c = if (direction == CrosswordDirection.ACROSS) col + i else col
                plane[r to c] = candidate.answer[i]
            }
            placed.add(Placed(candidate, row, col, direction))
        }

        // Seed with the longest word, centered horizontally at the origin.
        val first = candidates.first()
        commit(first, row = 0, col = 0, direction = CrosswordDirection.ACROSS)

        for (candidate in candidates.drop(1)) {
            if (placed.size >= targetEntries) break
            var placedThisOne = false

            outer@ for (existing in placed) {
                for ((existingIndex, existingChar) in existing.candidate.answer.withIndex()) {
                    val matchIndex = candidate.answer.indices.firstOrNull { candidate.answer[it] == existingChar } ?: continue
                    val newDirection = if (existing.direction == CrosswordDirection.ACROSS) CrosswordDirection.DOWN else CrosswordDirection.ACROSS
                    val existingR = if (existing.direction == CrosswordDirection.DOWN) existing.row + existingIndex else existing.row
                    val existingC = if (existing.direction == CrosswordDirection.ACROSS) existing.col + existingIndex else existing.col
                    val newRow = if (newDirection == CrosswordDirection.DOWN) existingR - matchIndex else existingR
                    val newCol = if (newDirection == CrosswordDirection.ACROSS) existingC - matchIndex else existingC

                    if (fits(candidate, newRow, newCol, newDirection)) {
                        commit(candidate, newRow, newCol, newDirection)
                        placedThisOne = true
                        break@outer
                    }
                }
            }
            if (!placedThisOne) continue
        }

        // Normalize to a 0-based bounding box, computed from every occupied cell
        // (not just placement anchors - an ACROSS word's row is fixed but a DOWN
        // word's column is fixed, so anchors alone don't give the true extents).
        val minRow = plane.keys.minOf { it.first }
        val minCol = plane.keys.minOf { it.second }
        val shiftedPlane = plane.mapKeys { (pos, _) -> (pos.first - minRow) to (pos.second - minCol) }

        val entries = placed.mapIndexed { index, p ->
            CrosswordEntry(
                id = "cw_$index",
                number = 0, // assigned below once cell numbering is known
                row = p.row - minRow,
                col = p.col - minCol,
                direction = p.direction,
                answer = p.candidate.answer,
                clue = p.candidate.clue,
            )
        }

        // Number entries in reading order (top-to-bottom, left-to-right); a
        // cell that starts more than one entry shares a single number.
        val startCells = entries.map { it.row to it.col }.distinct()
            .sortedWith(compareBy({ it.first }, { it.second }))
        val numberByCell = startCells.withIndex().associate { (i, cell) -> cell to i + 1 }
        val numberedEntries = entries.map { it.copy(number = numberByCell.getValue(it.row to it.col)) }

        val rows = (shiftedPlane.keys.maxOfOrNull { it.first } ?: 0) + 1
        val cols = (shiftedPlane.keys.maxOfOrNull { it.second } ?: 0) + 1
        val cells = shiftedPlane.mapValues { (pos, letter) ->
            CrosswordCell(row = pos.first, col = pos.second, letter = letter, number = numberByCell[pos])
        }

        return CrosswordPuzzle(rows = rows, cols = cols, cells = cells, entries = numberedEntries)
    }
}
