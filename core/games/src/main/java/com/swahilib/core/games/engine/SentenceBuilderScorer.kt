package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.SentenceQuestion
import com.swahilib.core.games.model.SentenceResult

object SentenceBuilderScorer {

    fun check(question: SentenceQuestion, submittedOrder: List<String>): Boolean =
        submittedOrder == question.correctOrder

    fun tally(
        results: List<Boolean>,
        difficulty: Difficulty,
        secondsSpent: Int,
    ): SentenceResult {
        val total = results.size
        val correct = results.count { it }
        val accuracy = if (total == 0) 0f else correct.toFloat() / total.toFloat()
        val isPerfect = total > 0 && correct == total
        val xpPerQuestion = RewardRules.activityXp(ActivityType.SENTENCE_BUILDER, difficulty) / total.coerceAtLeast(1)

        return SentenceResult(
            totalQuestions = total,
            correctAnswers = correct,
            accuracy = accuracy,
            isPerfect = isPerfect,
            xpEarned = (xpPerQuestion * correct).coerceAtLeast(if (correct > 0) 1 else 0),
            secondsSpent = secondsSpent,
        )
    }
}
