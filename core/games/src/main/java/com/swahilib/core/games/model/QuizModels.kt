package com.swahilib.core.games.model

import com.swahilib.core.engagement.model.Difficulty
import kotlinx.serialization.Serializable

/**
 * Sprint 2 - Quiz Engine domain models. Pure Kotlin, no Room/Compose deps,
 * so QuizGenerator output can be unit tested and reused by any UI.
 *
 * `ActivityType.VOCABULARY_QUIZ` (core:engagement) is the umbrella activity;
 * a single quiz session is made of several [QuizQuestion]s, each in one of
 * the formats below. Listening Quiz is intentionally omitted (audio content
 * doesn't exist yet - flagged as "future" in CLAUDE.md).
 */
enum class QuizFormat {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    FILL_IN_BLANK,
    MATCH_WORDS,
}

/** One selectable option in a multiple-choice or match-words question. */
data class QuizOption(
    val id: String,
    val text: String,
)

/**
 * A single quiz question. Shape is deliberately generic (options + correct
 * id set) so one composable can render MULTIPLE_CHOICE and MATCH_WORDS with
 * the same underlying model, and FILL_IN_BLANK/TRUE_FALSE are just the
 * degenerate 2-and-N option cases.
 */
data class QuizQuestion(
    val id: String,
    val format: QuizFormat,
    val prompt: String,
    val options: List<QuizOption> = emptyList(),
    val correctOptionIds: Set<String> = emptySet(),
    /** Expected free-text answer, only populated for FILL_IN_BLANK. */
    val expectedText: String? = null,
    /**
     * MATCH_WORDS only: [matchLeft] and [matchRight] share ids per correct
     * pair (e.g. word rid "42" appears in both lists). [matchRight] should be
     * displayed in a different order than [matchLeft] - a pair is correct
     * when the user connects two options with the same id.
     */
    val matchLeft: List<QuizOption> = emptyList(),
    val matchRight: List<QuizOption> = emptyList(),
    val explanation: String,
    val sourceWordRid: Int? = null,
)

/** A generated, ready-to-play set of questions. */
data class QuizSet(
    val id: String,
    val difficulty: Difficulty,
    val questions: List<QuizQuestion>,
)

/** One user answer, captured as it's submitted during play. */
@Serializable
data class QuizAnswer(
    val questionId: String,
    val selectedOptionIds: Set<String> = emptySet(),
    val typedText: String? = null,
    val correct: Boolean,
)

/** Final tally once every question in a [QuizSet] has been answered. */
data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val isPerfect: Boolean,
    val xpEarned: Int,
    val secondsSpent: Int,
)
