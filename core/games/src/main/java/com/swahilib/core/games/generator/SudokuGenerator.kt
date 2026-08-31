package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.SudokuDirection
import com.swahilib.core.games.model.SudokuPuzzle
import com.swahilib.core.games.model.SudokuTheme
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SudokuGenerator @Inject constructor(
    private val wordDao: WordDao,
) {
    private val fillerLetters = "AAAEEEIIOOUUNNMMKKWWLLTTSSBBRRDDGGHHYYFFPPVVJJCCZZ"

    suspend fun generate(
        difficulty: Difficulty = Difficulty.BEGINNER,
        theme: SudokuTheme = SudokuTheme.RANDOM,
        wordCount: Int = 8,
        seed: Long = System.currentTimeMillis(),
        easyFillerPool: Boolean = false,
    ): SudokuPuzzle {
        val random = Random(seed)
        val gridSize = when (difficulty) {
            Difficulty.BEGINNER -> 10
            Difficulty.INTERMEDIATE -> 12
            Difficulty.ADVANCED -> 14
        }
        // Diagonals are available at every difficulty now.
        val allowedDirections = SudokuDirection.entries

        val candidates = sourceWords(theme, gridSize, random)
        if (candidates.isEmpty()) {
            return SudokuPuzzle(size = gridSize, grid = emptyList(), words = emptyList())
        }

        val grid = Array(gridSize) { CharArray(gridSize) { ' ' } }
        val placed = mutableListOf<PlacedWord>()

        for ((word, clue) in candidates.shuffled(random).take(wordCount)) {
            if (word.length > gridSize) continue
            val placement = tryPlace(grid, word, gridSize, allowedDirections, random)
            if (placement != null) {
                val (row, col, direction) = placement
                placed.add(PlacedWord(word = word, row = row, col = col, direction = direction, clue = clue))
            }
        }

        val fillerPool = if (easyFillerPool) {
            placed.flatMap { it.word.toList() }.distinct().joinToString("").ifEmpty { fillerLetters }
        } else {
            fillerLetters
        }
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (grid[r][c] == ' ') grid[r][c] = fillerPool[random.nextInt(fillerPool.length)]
            }
        }

        return SudokuPuzzle(size = gridSize, grid = grid.map { it.toList() }, words = placed)
    }

    private suspend fun sourceWords(theme: SudokuTheme, gridSize: Int, random: Random): List<Pair<String, String>> {
        if (theme != SudokuTheme.RANDOM) {
            val themed = ThemeWordBank.wordsFor(theme).map { (w, c) -> w.uppercase() to c }
            if (themed.isNotEmpty()) return themed
        }
        return wordDao.getAll().first().mapNotNull { entity ->
            val answer = entity.title?.trim()?.uppercase().orEmpty()
            val clue = entity.definitionText()
            if (answer.isNotBlank() && answer.all { it.isLetter() } && answer.length in 3..gridSize && clue != null) {
                answer to clue
            } else null
        }
    }

    private fun tryPlace(
        grid: Array<CharArray>,
        word: String,
        gridSize: Int,
        allowedDirections: List<SudokuDirection>,
        random: Random,
        maxAttempts: Int = 60,
    ): Triple<Int, Int, SudokuDirection>? {
        val len = word.length

        fun startRange(d: Int): IntRange {
            val lo = maxOf(0, -d * (len - 1))
            val hi = minOf(gridSize - 1, gridSize - 1 - d * (len - 1))
            return if (lo > hi) IntRange.EMPTY else lo..hi
        }

        repeat(maxAttempts) {
            val direction = allowedDirections.random(random)
            val rowRange = startRange(direction.dRow)
            val colRange = startRange(direction.dCol)
            if (rowRange.isEmpty() || colRange.isEmpty()) return@repeat
            val row = random.nextInt(rowRange.first, rowRange.last + 1)
            val col = random.nextInt(colRange.first, colRange.last + 1)

            var fits = true
            for (i in word.indices) {
                val r = row + direction.dRow * i
                val c = col + direction.dCol * i
                val existing = grid[r][c]
                if (existing != ' ' && existing != word[i]) {
                    fits = false
                    break
                }
            }
            if (fits) {
                for (i in word.indices) {
                    val r = row + direction.dRow * i
                    val c = col + direction.dCol * i
                    grid[r][c] = word[i]
                }
                return Triple(row, col, direction)
            }
        }
        return null
    }
}
