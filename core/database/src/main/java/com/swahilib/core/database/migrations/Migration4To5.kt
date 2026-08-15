package com.swahilib.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Introduces the engagement system: user progress/XP, achievements,
 * challenges (with their activities), daily activity summaries, and the
 * unified learning history table.
 */
object Migration4To5 : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `user_progress` (
                `id` INTEGER NOT NULL,
                `totalXp` INTEGER NOT NULL,
                `coins` INTEGER NOT NULL,
                `level` INTEGER NOT NULL,
                `challengesCompleted` INTEGER NOT NULL,
                `activitiesCompleted` INTEGER NOT NULL,
                `wordsLearned` INTEGER NOT NULL,
                `gamesPlayed` INTEGER NOT NULL,
                `totalLearningSeconds` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `xp_events` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `amount` INTEGER NOT NULL,
                `source` TEXT NOT NULL,
                `activityType` TEXT,
                `referenceId` TEXT,
                `date` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `secondsSpent` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_xp_events_date` ON `xp_events` (`date`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_xp_events_source` ON `xp_events` (`source`)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `achievement_records` (
                `achievementId` TEXT NOT NULL,
                `unlockedAt` INTEGER NOT NULL,
                `xpAwarded` INTEGER NOT NULL,
                `coinsAwarded` INTEGER NOT NULL,
                PRIMARY KEY(`achievementId`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `challenges` (
                `id` TEXT NOT NULL,
                `scope` TEXT NOT NULL,
                `periodKey` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `difficulty` TEXT NOT NULL,
                `xpReward` INTEGER NOT NULL,
                `coinReward` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `expiresAt` INTEGER NOT NULL,
                `completed` INTEGER NOT NULL,
                `completedAt` INTEGER,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_challenges_scope_periodKey` " +
                "ON `challenges` (`scope`, `periodKey`)"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_challenges_expiresAt` ON `challenges` (`expiresAt`)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `challenge_activities` (
                `id` TEXT NOT NULL,
                `challengeId` TEXT NOT NULL,
                `orderIndex` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `estimatedSeconds` INTEGER NOT NULL,
                `xpReward` INTEGER NOT NULL,
                `completed` INTEGER NOT NULL,
                `completedAt` INTEGER,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_challenge_activities_challengeId` ON `challenge_activities` (`challengeId`)")
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_challenge_activities_challengeId_orderIndex` " +
                "ON `challenge_activities` (`challengeId`, `orderIndex`)"
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `daily_activity` (
                `date` TEXT NOT NULL,
                `xpEarned` INTEGER NOT NULL,
                `activitiesCompleted` INTEGER NOT NULL,
                `secondsSpent` INTEGER NOT NULL,
                `quizzesCorrect` INTEGER NOT NULL,
                `quizzesTotal` INTEGER NOT NULL,
                `wordsLearned` INTEGER NOT NULL,
                `gamesPlayed` INTEGER NOT NULL,
                `challengesCompleted` INTEGER NOT NULL,
                `visited` INTEGER NOT NULL,
                PRIMARY KEY(`date`)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `learning_history` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `type` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `referenceId` TEXT,
                `score` INTEGER,
                `maxScore` INTEGER,
                `xpEarned` INTEGER NOT NULL,
                `secondsSpent` INTEGER NOT NULL,
                `date` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_history_date` ON `learning_history` (`date`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_learning_history_type` ON `learning_history` (`type`)")
    }
}
