package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.QuizAnswer
import com.swahilib.core.games.model.QuizOption
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizResult

object QuizScorer {
    fun checkChoice(question: QuizQuestion, selectedOptionId: String): QuizAnswer =
        QuizAnswer(
            questionId = question.id,
            selectedOptionIds = setOf(selectedOptionId),
            correct = selectedOptionId in question.correctOptionIds,
        )

    fun checkTyped(question: QuizQuestion, typed: String): QuizAnswer =
        QuizAnswer(
            questionId = question.id,
            typedText = typed,
            correct = normalize(typed) == normalize(question.expectedText.orEmpty()) &&
                typed.isNotBlank(),
        )

    fun checkMatches(question: QuizQuestion, connectedPairs: Map<String, String>): QuizAnswer {
        val allCorrect = question.matchLeft.isNotEmpty() &&
            question.matchLeft.all { left -> connectedPairs[left.id] == left.id }
        return QuizAnswer(
            questionId = question.id,
            selectedOptionIds = connectedPairs.values.toSet(),
            correct = allCorrect,
        )
    }

    fun tally(
        answers: List<QuizAnswer>,
        difficulty: Difficulty,
        secondsSpent: Int,
    ): QuizResult {
        val total = answers.size
        val correct = answers.count { it.correct }
        val accuracy = if (total == 0) 0f else correct.toFloat() / total.toFloat()
        val isPerfect = total > 0 && correct == total

        val baseXpPerQuestion = RewardRules.activityXp(ActivityType.VOCABULARY_QUIZ, difficulty) / total.coerceAtLeast(1)
        val xpEarned = (baseXpPerQuestion * correct).coerceAtLeast(if (correct > 0) 1 else 0)

        return QuizResult(
            totalQuestions = total,
            correctAnswers = correct,
            accuracy = accuracy,
            isPerfect = isPerfect,
            xpEarned = xpEarned,
            secondsSpent = secondsSpent,
        )
    }

    private fun normalize(text: String): String = text.trim().lowercase()
}

fun QuizOption.isCorrectFor(question: QuizQuestion): Boolean = id in question.correctOptionIds
