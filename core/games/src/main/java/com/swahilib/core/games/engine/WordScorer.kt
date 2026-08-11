package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.ScrambledWord
import com.swahilib.core.games.model.WordRoundResult
import com.swahilib.core.games.model.WordSessionResult

object WordScorer {
    private const val HINT_PENALTY = 0.2f
    private const val MIN_XP_SHARE = 0.2f

    fun checkAnswer(word: ScrambledWord, typed: String, hintsUsed: Int, secondsSpent: Int): WordRoundResult =
        WordRoundResult(
            wordId = word.id,
            correct = typed.trim().equals(word.answer, ignoreCase = true),
            hintsUsed = hintsUsed,
            secondsSpent = secondsSpent,
        )

    fun tally(
        rounds: List<WordRoundResult>,
        difficulty: Difficulty,
        secondsSpent: Int,
    ): WordSessionResult {
        val total = rounds.size
        val correct = rounds.count { it.correct }
        val xpPerRound = RewardRules.activityXp(ActivityType.WORD_BUILDER, difficulty) / total.coerceAtLeast(1)

        val xpEarned = rounds.filter { it.correct }.sumOf { round ->
            val penalty = (1f - HINT_PENALTY * round.hintsUsed).coerceAtLeast(MIN_XP_SHARE)
            (xpPerRound * penalty).toInt().coerceAtLeast(1)
        }

        return WordSessionResult(
            totalWords = total,
            correctWords = correct,
            totalHintsUsed = rounds.sumOf { it.hintsUsed },
            xpEarned = xpEarned,
            secondsSpent = secondsSpent,
            isPerfect = total > 0 && correct == total && rounds.all { it.hintsUsed == 0 },
        )
    }
}
