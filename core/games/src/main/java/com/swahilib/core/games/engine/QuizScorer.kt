package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.QuizAnswer
import com.swahilib.core.games.model.QuizOption
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizResult

/**
 * Stateless answer-checking + tallying. The ViewModel owns session state
 * (current index, collected answers); this just judges one answer at a time
 * and folds a finished session into a [QuizResult].
 */
object QuizScorer {

    /** Checks a multiple-choice / true-false selection against the question. */
    fun checkChoice(question: QuizQuestion, selectedOptionId: String): QuizAnswer =
        QuizAnswer(
            questionId = question.id,
            selectedOptionIds = setOf(selectedOptionId),
            correct = selectedOptionId in question.correctOptionIds,
        )

    /** Checks a typed fill-in-the-blank answer, forgiving case/whitespace. */
    fun checkTyped(question: QuizQuestion, typed: String): QuizAnswer =
        QuizAnswer(
            questionId = question.id,
            typedText = typed,
            correct = normalize(typed) == normalize(question.expectedText.orEmpty()) &&
                typed.isNotBlank(),
        )

    /** Checks a MATCH_WORDS question given the pairs the user connected (leftId to rightId). */
    fun checkMatches(question: QuizQuestion, connectedPairs: Map<String, String>): QuizAnswer {
        val allCorrect = question.matchLeft.isNotEmpty() &&
            question.matchLeft.all { left -> connectedPairs[left.id] == left.id }
        return QuizAnswer(
            questionId = question.id,
            selectedOptionIds = connectedPairs.values.toSet(),
            correct = allCorrect,
        )
    }

    /** Folds a completed set of answers into XP + accuracy for the session. */
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

/** Convenience for UI code that needs to know an option's correctness after reveal. */
fun QuizOption.isCorrectFor(question: QuizQuestion): Boolean = id in question.correctOptionIds
