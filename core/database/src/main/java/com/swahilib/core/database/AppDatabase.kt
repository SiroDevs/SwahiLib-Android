package com.swahilib.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.swahilib.core.database.daos.AchievementRecordDao
import com.swahilib.core.database.daos.ChallengeDao
import com.swahilib.core.database.daos.DailyActivityDao
import com.swahilib.core.database.daos.DailyContentDao
import com.swahilib.core.database.daos.GameProgressDao
import com.swahilib.core.database.daos.HistoryDao
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.daos.LearningHistoryDao
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.SayingDao
import com.swahilib.core.database.daos.SearchDao
import com.swahilib.core.database.daos.UserProgressDao
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.daos.XpEventDao
import com.swahilib.core.database.migrations.ALL_MIGRATIONS
import com.swahilib.core.database.model.AchievementRecordEntity
import com.swahilib.core.database.model.ChallengeActivityEntity
import com.swahilib.core.database.model.ChallengeEntity
import com.swahilib.core.database.model.DailyActivityEntity
import com.swahilib.core.database.model.DailyContentEntity
import com.swahilib.core.database.model.GameLevelProgressEntity
import com.swahilib.core.database.model.GameSessionStateEntity
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
        GameLevelProgressEntity::class,
        GameSessionStateEntity::class,
    ],
    version = 6,
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
    abstract fun gameProgressDao(): GameProgressDao

    companion object {
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
