package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.SudokuResult
import kotlin.math.sign

object SudokuScorer {
    fun matchSelection(
        words: List<PlacedWord>,
        startRow: Int,
        startCol: Int,
        endRow: Int,
        endCol: Int,
    ): PlacedWord? {
        val dRow = (endRow - startRow).sign
        val dCol = (endCol - startCol).sign
        if (dRow == 0 && dCol == 0) return null

        return words.firstOrNull { placed ->
            if (placed.found) return@firstOrNull false
            val lastRow = placed.row + placed.direction.dRow * (placed.word.length - 1)
            val lastCol = placed.col + placed.direction.dCol * (placed.word.length - 1)
            (placed.row == startRow && placed.col == startCol && lastRow == endRow && lastCol == endCol) ||
                (placed.row == endRow && placed.col == endCol && lastRow == startRow && lastCol == startCol)
        }
    }

    fun tally(words: List<PlacedWord>, difficulty: Difficulty, secondsSpent: Int): SudokuResult {
        val total = words.size
        val found = words.count { it.found }
        val isPerfect = total > 0 && found == total
        val xpPerWord = RewardRules.activityXp(ActivityType.SUDOKU, difficulty) / total.coerceAtLeast(1)

        return SudokuResult(
            totalWords = total,
            foundWords = found,
            isPerfect = isPerfect,
            xpEarned = (xpPerWord * found).coerceAtLeast(if (found > 0) 1 else 0),
            secondsSpent = secondsSpent,
        )
    }
}
