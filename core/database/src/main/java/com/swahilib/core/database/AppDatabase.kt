package com.swahilib.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.swahilib.core.database.daos.content.HistoryDao
import com.swahilib.core.database.daos.content.IdiomDao
import com.swahilib.core.database.daos.content.ProverbDao
import com.swahilib.core.database.daos.content.SayingDao
import com.swahilib.core.database.daos.content.SearchDao
import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.database.daos.daily.DailyActivityDao
import com.swahilib.core.database.daos.daily.DailyContentDao
import com.swahilib.core.database.daos.game.AchievementRecordDao
import com.swahilib.core.database.daos.game.ChallengeDao
import com.swahilib.core.database.daos.game.GameProgressDao
import com.swahilib.core.database.daos.game.LearningHistoryDao
import com.swahilib.core.database.daos.game.UserProgressDao
import com.swahilib.core.database.daos.game.XpEventDao
import com.swahilib.core.database.entities.content.HistoryEntity
import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.database.entities.content.SearchEntity
import com.swahilib.core.database.entities.content.WordEntity
import com.swahilib.core.database.entities.daily.DailyActivityEntity
import com.swahilib.core.database.entities.daily.DailyContentEntity
import com.swahilib.core.database.entities.game.AchievementRecordEntity
import com.swahilib.core.database.entities.game.ChallengeActivityEntity
import com.swahilib.core.database.entities.game.ChallengeEntity
import com.swahilib.core.database.entities.game.GameLevelProgressEntity
import com.swahilib.core.database.entities.game.GameSessionStateEntity
import com.swahilib.core.database.entities.game.LearningHistoryEntity
import com.swahilib.core.database.entities.game.UserProgressEntity
import com.swahilib.core.database.entities.game.XpEventEntity
import com.swahilib.core.database.migrations.ALL_MIGRATIONS

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
