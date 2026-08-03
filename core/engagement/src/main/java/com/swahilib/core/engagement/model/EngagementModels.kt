package com.swahilib.core.engagement.model

/**
 * Pure-Kotlin engagement domain models. Kept separate from the Room entities in
 * `core:database` so the engines (XP, Rewards, Achievements, Challenges,
 * Statistics) can operate on stable value types regardless of storage format.
 */

enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

enum class ChallengeScope { DAILY, WEEKLY, MONTHLY, PRACTICE, SEASONAL }

/** Every learning activity that can appear inside a challenge. Adding a new
 *  game (Sprint 2+) means adding an entry here and a handler in ActivityRegistry. */
enum class ActivityType {
    VOCABULARY_QUIZ,
    WORD_BUILDER,
    CROSSWORD,
    WORD_SEARCH,
    SENTENCE_BUILDER,
    SPELLING_CHALLENGE,
    PROVERB_CHALLENGE,
    HANGMAN,
    DAILY_READ,
    STREAK_VISIT,
    CUSTOM,
}

data class ChallengeActivity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val estimatedSeconds: Int,
    val xpReward: Int,
    val completed: Boolean = false,
)

data class Challenge(
    val id: String,
    val scope: ChallengeScope,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val xpReward: Int,
    val coinReward: Int,
    val activities: List<ChallengeActivity>,
    val createdAt: Long,
    val expiresAt: Long,
    val completed: Boolean = false,
    val completedAt: Long? = null,
) {
    val estimatedSeconds: Int get() = activities.sumOf { it.estimatedSeconds }
    val progress: Float get() =
        if (activities.isEmpty()) 0f
        else activities.count { it.completed }.toFloat() / activities.size
}

/**
 * Immutable snapshot of a user's engagement state. Derived from
 * `UserProgressEntity` + streak counters in PrefsRepo. Level is computed from
 * total XP via [LevelCurve] so it never disagrees with XP.
 */
data class UserProgress(
    val totalXp: Long,
    val coins: Long,
    val level: Int,
    val xpIntoLevel: Long,
    val xpForNextLevel: Long,
    val currentStreak: Int,
    val bestStreak: Int,
    val challengesCompleted: Int,
    val activitiesCompleted: Int,
) {
    val progressToNextLevel: Float get() =
        if (xpForNextLevel == 0L) 0f
        else (xpIntoLevel.toFloat() / xpForNextLevel.toFloat()).coerceIn(0f, 1f)
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val xpReward: Int,
    val coinReward: Int,
    val unlockedAt: Long? = null,
) {
    val unlocked: Boolean get() = unlockedAt != null
}

/** One point in the daily-activity chart. */
data class DailyActivitySnapshot(
    val date: String,
    val xpEarned: Long,
    val activitiesCompleted: Int,
    val secondsSpent: Long,
    val quizzesCorrect: Int,
    val quizzesTotal: Int,
    val wordsLearned: Int,
    val gamesPlayed: Int,
)

/** Aggregated statistics for the dashboard. */
data class StatisticsSummary(
    val totalLearningSeconds: Long,
    val quizAccuracy: Float,
    val gamesPlayed: Int,
    val wordsLearned: Int,
    val weeklyActivity: List<DailyActivitySnapshot>,
    val activeDaysThisWeek: Int,
)

/** Payload describing why XP was awarded. Fed into XpEngine.award(). */
data class XpAward(
    val source: XpSource,
    val amount: Int,
    val referenceId: String? = null,
    val activityType: ActivityType? = null,
    val secondsSpent: Int = 0,
)

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

/** Result of an award() call - clients can react to level ups & new badges. */
data class AwardResult(
    val progress: UserProgress,
    val leveledUp: Boolean,
    val previousLevel: Int,
    val unlockedAchievements: List<Achievement>,
    val coinsAwarded: Int,
)
