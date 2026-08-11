package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.SpellingQuestion
import com.swahilib.core.games.model.SpellingResult
import com.swahilib.core.games.model.SpellingRoundResult
import kotlin.math.max

object SpellingScorer {
    private const val PARTIAL_CREDIT_FLOOR = 0.5f
    private const val HINT_PENALTY_PER_LETTER = 0.15f

    fun checkAnswer(question: SpellingQuestion, typed: String, hintsUsed: Int): SpellingRoundResult {
        val normalizedTyped = typed.trim().lowercase()
        val normalizedAnswer = question.answer.trim().lowercase()
        val fullyCorrect = normalizedTyped == normalizedAnswer && hintsUsed == 0

        val similarity = if (normalizedAnswer.isEmpty()) {
            0f
        } else {
            1f - levenshtein(normalizedTyped, normalizedAnswer).toFloat() / max(normalizedAnswer.length, normalizedTyped.length).toFloat()
        }
        val hintAdjusted = (similarity - HINT_PENALTY_PER_LETTER * hintsUsed).coerceIn(0f, 1f)
        val credit = when {
            normalizedTyped == normalizedAnswer -> (1f - HINT_PENALTY_PER_LETTER * hintsUsed).coerceIn(0f, 1f)
            hintAdjusted < PARTIAL_CREDIT_FLOOR -> 0f
            else -> hintAdjusted
        }

        return SpellingRoundResult(
            questionId = question.id,
            typed = typed,
            fullyCorrect = fullyCorrect,
            partialCredit = credit,
            hintsUsed = hintsUsed,
        )
    }

    fun tally(rounds: List<SpellingRoundResult>, difficulty: Difficulty, secondsSpent: Int): SpellingResult {
        val total = rounds.size
        val fullyCorrect = rounds.count { it.fullyCorrect }
        val avgCredit = if (total == 0) 0f else rounds.sumOf { it.partialCredit.toDouble() }.toFloat() / total
        val xpPerQuestion = RewardRules.activityXp(ActivityType.SPELLING_CHALLENGE, difficulty).toFloat() / total.coerceAtLeast(1)
        val xpEarned = rounds.sumOf { (xpPerQuestion * it.partialCredit).toInt().coerceAtLeast(0).toLong() }.toInt()

        return SpellingResult(
            totalQuestions = total,
            fullyCorrectCount = fullyCorrect,
            averageCredit = avgCredit,
            isPerfect = total > 0 && fullyCorrect == total,
            xpEarned = xpEarned,
            secondsSpent = secondsSpent,
        )
    }

    private fun levenshtein(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length

        var previous = IntArray(b.length + 1) { it }
        var current = IntArray(b.length + 1)

        for (i in 1..a.length) {
            current[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                current[j] = minOf(
                    current[j - 1] + 1,
                    previous[j] + 1,
                    previous[j - 1] + cost,
                )
            }
            val tmp = previous
            previous = current
            current = tmp
        }
        return previous[b.length]
    }
}
