package com.swahilib.core.engagement.engine

import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.ChallengeScope
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpSource

object RewardRules {

    fun activityXp(type: ActivityType, difficulty: Difficulty = Difficulty.BEGINNER): Int {
        val base = when (type) {
            ActivityType.DAILY_READ -> 5
            ActivityType.STREAK_VISIT -> 5
            ActivityType.VOCABULARY_QUIZ -> 15
            ActivityType.SPELLING_CHALLENGE -> 15
            ActivityType.WORD_BUILDER -> 20
            ActivityType.SUDOKU -> 20
            ActivityType.SENTENCE_BUILDER -> 25
            ActivityType.PROVERB_CHALLENGE -> 25
            ActivityType.CROSSWORD -> 30
            ActivityType.HANGMAN -> 20
            ActivityType.CUSTOM -> 10
        }
        return (base * difficultyMultiplier(difficulty)).toInt()
    }

    fun challengeCompletionXp(scope: ChallengeScope, activityXpTotal: Int): Int = when (scope) {
        ChallengeScope.DAILY -> activityXpTotal / 4
        ChallengeScope.WEEKLY -> activityXpTotal / 3
        ChallengeScope.MONTHLY -> activityXpTotal / 2
        ChallengeScope.PRACTICE -> 0
        ChallengeScope.SEASONAL -> (activityXpTotal * 0.75f).toInt() // limited-time, so generous
    }

    fun challengeCompletionCoins(scope: ChallengeScope): Int = when (scope) {
        ChallengeScope.DAILY -> 5
        ChallengeScope.WEEKLY -> 25
        ChallengeScope.MONTHLY -> 100
        ChallengeScope.PRACTICE -> 0
        ChallengeScope.SEASONAL -> 50
    }

    fun dailyLoginCoins(streakDay: Int): Int = when {
        streakDay <= 1 -> 2
        streakDay < 7 -> 3
        streakDay < 30 -> 5
        streakDay < 100 -> 8
        else -> 12
    }

    fun dailyLoginXp(streakDay: Int): Int = when {
        streakDay <= 1 -> 10
        streakDay < 7 -> 15
        streakDay < 30 -> 25
        else -> 40
    }

    fun streakMilestoneXp(streakDay: Int): Int = when (streakDay) {
        3 -> 25
        7 -> 75
        14 -> 150
        30 -> 400
        60 -> 800
        100 -> 1500
        365 -> 5000
        else -> 0
    }

    const val HINT_COST = 5

    fun sourceMultiplier(source: XpSource): Float = when (source) {
        XpSource.PERFECT_QUIZ -> 1.25f
        XpSource.CHALLENGE_COMPLETE -> 1.1f
        XpSource.ACHIEVEMENT_UNLOCK -> 1f
        else -> 1f
    }

    private fun difficultyMultiplier(d: Difficulty): Float = when (d) {
        Difficulty.BEGINNER -> 1f
        Difficulty.INTERMEDIATE -> 1.5f
        Difficulty.ADVANCED -> 2f
    }
}
