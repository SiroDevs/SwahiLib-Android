package com.swahilib.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.swahilib.core.database.daos.AchievementRecordDao
import com.swahilib.core.database.daos.ChallengeDao
import com.swahilib.core.database.daos.DailyActivityDao
import com.swahilib.core.database.daos.DailyContentDao
import com.swahilib.core.database.daos.HistoryDao
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.daos.LearningHistoryDao
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.SayingDao
import com.swahilib.core.database.daos.SearchDao
import com.swahilib.core.database.daos.UserProgressDao
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.daos.XpEventDao
import com.swahilib.core.database.model.AchievementRecordEntity
import com.swahilib.core.database.model.ChallengeActivityEntity
import com.swahilib.core.database.model.ChallengeEntity
import com.swahilib.core.database.model.DailyActivityEntity
import com.swahilib.core.database.model.DailyContentEntity
import com.swahilib.core.database.model.HistoryEntity
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.LearningHistoryEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.SearchEntity
import com.swahilib.core.database.model.UserProgressEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.database.model.XpEventEntity

@Database(
    entities = [
        HistoryEntity::class,
        IdiomEntity::class,
        ProverbEntity::class,
        SayingEntity::class,
        SearchEntity::class,
        WordEntity::class,
        DailyContentEntity::class,
        UserProgressEntity::class,
        XpEventEntity::class,
        AchievementRecordEntity::class,
        ChallengeEntity::class,
        ChallengeActivityEntity::class,
        DailyActivityEntity::class,
        LearningHistoryEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historiesDao(): HistoryDao
    abstract fun idiomsDao(): IdiomDao
    abstract fun proverbsDao(): ProverbDao
    abstract fun sayingsDao(): SayingDao
    abstract fun searchesDao(): SearchDao
    abstract fun wordsDao(): WordDao
    abstract fun dailyContentDao(): DailyContentDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun xpEventDao(): XpEventDao
    abstract fun achievementRecordDao(): AchievementRecordDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun learningHistoryDao(): LearningHistoryDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN english TEXT")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_content` (
                        `id` INTEGER NOT NULL,
                        `date` TEXT NOT NULL,
                        `wordRid` INTEGER NOT NULL,
                        `wordMeaning` TEXT NOT NULL,
                        `proverbRid` INTEGER NOT NULL,
                        `proverbMeaning` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
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

        val ALL_MIGRATIONS = arrayOf(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)

        @Volatile private var widgetInstance: AppDatabase? = null

        fun getInstanceForWidget(context: Context): AppDatabase =
            widgetInstance ?: synchronized(this) {
                widgetInstance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "SwahiliLibrary"
                )
                    .addMigrations(*ALL_MIGRATIONS)
                    .build()
                    .also { widgetInstance = it }
            }
    }
}
