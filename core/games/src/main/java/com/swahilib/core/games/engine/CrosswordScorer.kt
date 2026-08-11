package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.CrosswordEntry
import com.swahilib.core.games.model.CrosswordResult

object CrosswordScorer {
    fun checkEntry(entry: CrosswordEntry, typed: String): Boolean =
        typed.trim().equals(entry.answer, ignoreCase = true)

    fun tally(
        entries: List<CrosswordEntry>,
        answers: Map<String, String>,
        difficulty: Difficulty,
        secondsSpent: Int,
    ): CrosswordResult {
        val total = entries.size
        val correct = entries.count { checkEntry(it, answers[it.id].orEmpty()) }
        val isPerfect = total > 0 && correct == total
        val xpPerEntry = RewardRules.activityXp(ActivityType.CROSSWORD, difficulty) / total.coerceAtLeast(1)

        return CrosswordResult(
            totalEntries = total,
            correctEntries = correct,
            isPerfect = isPerfect,
            xpEarned = (xpPerEntry * correct).coerceAtLeast(if (correct > 0) 1 else 0),
            secondsSpent = secondsSpent,
        )
    }
}
