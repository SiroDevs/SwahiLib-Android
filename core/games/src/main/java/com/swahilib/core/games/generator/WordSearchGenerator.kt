package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.WordSearchDirection
import com.swahilib.core.games.model.WordSearchPuzzle
import com.swahilib.core.games.model.WordSearchTheme
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class WordSearchGenerator @Inject constructor(
    private val wordDao: WordDao,
) {
    // Weighted toward common Swahili letters so filler cells don't look out of place.
    private val fillerLetters = "AAAEEEIIOOUUNNMMKKWWLLTTSSBBRRDDGGHHYYFFPPVVJJCCZZ"

    suspend fun generate(
        difficulty: Difficulty = Difficulty.BEGINNER,
        theme: WordSearchTheme = WordSearchTheme.RANDOM,
        wordCount: Int = 8,
        seed: Long = System.currentTimeMillis(),
    ): WordSearchPuzzle {
        val random = Random(seed)
        val gridSize = when (difficulty) {
            Difficulty.BEGINNER -> 10
            Difficulty.INTERMEDIATE -> 12
            Difficulty.ADVANCED -> 14
        }
        val allowedDirections = when (difficulty) {
            Difficulty.BEGINNER -> listOf(WordSearchDirection.HORIZONTAL, WordSearchDirection.VERTICAL)
            else -> WordSearchDirection.entries
        }

        val candidates = sourceWords(theme, gridSize, random)
        if (candidates.isEmpty()) {
            return WordSearchPuzzle(size = gridSize, grid = emptyList(), words = emptyList())
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

        // Fill remaining blanks with random letters so the puzzle doesn't reveal word shapes.
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (grid[r][c] == ' ') grid[r][c] = fillerLetters[random.nextInt(fillerLetters.length)]
            }
        }

        return WordSearchPuzzle(size = gridSize, grid = grid.map { it.toList() }, words = placed)
    }

    private suspend fun sourceWords(theme: WordSearchTheme, gridSize: Int, random: Random): List<Pair<String, String>> {
        if (theme != WordSearchTheme.RANDOM) {
            val themed = ThemeWordBank.wordsFor(theme).map { (w, c) -> w.uppercase() to c }
            if (themed.isNotEmpty()) return themed
        }
        return wordDao.getAll().first().mapNotNull { entity ->
            val answer = entity.title?.trim()?.uppercase().orEmpty()
            val clue = entity.meaning?.takeIf { it.isNotBlank() } ?: entity.english?.takeIf { it.isNotBlank() }
            if (answer.isNotBlank() && answer.all { it.isLetter() } && answer.length in 3..gridSize && clue != null) {
                answer to clue
            } else null
        }
    }

    private fun tryPlace(
        grid: Array<CharArray>,
        word: String,
        gridSize: Int,
        allowedDirections: List<WordSearchDirection>,
        random: Random,
        maxAttempts: Int = 60,
    ): Triple<Int, Int, WordSearchDirection>? {
        repeat(maxAttempts) {
            val direction = allowedDirections.random(random)
            val maxRow = gridSize - (direction.dRow * (word.length - 1))
            val maxCol = gridSize - (direction.dCol * (word.length - 1))
            if (maxRow <= 0 || maxCol <= 0) return@repeat
            val row = random.nextInt(maxRow)
            val col = random.nextInt(maxCol)

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
