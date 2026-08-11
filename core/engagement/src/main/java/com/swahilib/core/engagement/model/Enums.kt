package com.swahilib.core.engagement.model

enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

enum class ChallengeScope { DAILY, WEEKLY, MONTHLY, PRACTICE, SEASONAL }

enum class ActivityType {
    VOCABULARY_QUIZ,
    WORD_BUILDER,
    CROSSWORD,
    SUDOKU,
    SENTENCE_BUILDER,
    SPELLING_CHALLENGE,
    PROVERB_CHALLENGE,
    HANGMAN,
    DAILY_READ,
    STREAK_VISIT,
    CUSTOM,
}

enum class XpSource {
    DAILY_LOGIN,
    STREAK_BONUS,
    ACTIVITY_COMPLETE,
    CHALLENGE_COMPLETE,
    ACHIEVEMENT_UNLOCK,
    PERFECT_QUIZ,
    WORD_LEARNED,
    MANUAL,
}
