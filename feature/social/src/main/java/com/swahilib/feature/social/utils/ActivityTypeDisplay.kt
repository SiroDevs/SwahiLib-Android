package com.swahilib.feature.social.utils

import com.swahilib.core.common.utils.Routes
import com.swahilib.core.engagement.model.ActivityType

/** The subset of [ActivityType] that has an actual game screen - the ones worth challenging a friend to. */
val challengeableActivityTypes = listOf(
    ActivityType.VOCABULARY_QUIZ,
    ActivityType.WORD_BUILDER,
    ActivityType.SENTENCE_BUILDER,
    ActivityType.SPELLING_CHALLENGE,
    ActivityType.CROSSWORD,
    ActivityType.SUDOKU,
    ActivityType.PROVERB_CHALLENGE,
    ActivityType.HANGMAN,
)

fun titleFor(type: ActivityType): String = when (type) {
    ActivityType.VOCABULARY_QUIZ -> "Jaribio la Msamiati"
    ActivityType.WORD_BUILDER -> "Jenga Maneno"
    ActivityType.SENTENCE_BUILDER -> "Jenga Sentensi"
    ActivityType.SPELLING_CHALLENGE -> "Tahajia (Spellcheck)"
    ActivityType.CROSSWORD -> "CrossWord"
    ActivityType.SUDOKU -> "Tafuta Maneno"
    ActivityType.PROVERB_CHALLENGE -> "Changamoto ya Methali"
    ActivityType.HANGMAN -> "Hangman"
    ActivityType.DAILY_READ, ActivityType.STREAK_VISIT, ActivityType.CUSTOM -> type.name
}

/** Freeplay route for practising/playing this activity type outside a challenge context. */
fun freeplayRouteFor(type: ActivityType): String = when (type) {
    ActivityType.VOCABULARY_QUIZ -> Routes.quizFreeplay()
    ActivityType.PROVERB_CHALLENGE -> Routes.quizFreeplay(source = "PROVERBS")
    ActivityType.WORD_BUILDER -> Routes.wordBuilderFreeplay()
    ActivityType.SENTENCE_BUILDER -> Routes.sentenceBuilderFreeplay()
    ActivityType.SPELLING_CHALLENGE -> Routes.spellingFreeplay()
    ActivityType.CROSSWORD -> Routes.crosswordFreeplay()
    ActivityType.SUDOKU -> Routes.wordSearchFreeplay()
    ActivityType.HANGMAN -> Routes.hangmanFreeplay()
    ActivityType.DAILY_READ, ActivityType.STREAK_VISIT, ActivityType.CUSTOM -> Routes.quizFreeplay()
}

/** Parses the free-text `activity_type` column back into an enum, falling back to VOCABULARY_QUIZ
 * for anything unrecognized (e.g. written by a future app version with a new activity type). */
fun activityTypeFrom(raw: String): ActivityType =
    ActivityType.entries.firstOrNull { it.name == raw } ?: ActivityType.VOCABULARY_QUIZ

fun difficultyTitle(name: String): String = when (name) {
    "BEGINNER" -> "Mwanzo"
    "INTERMEDIATE" -> "Wastani"
    "ADVANCED" -> "Juu"
    else -> name
}
