package com.swahilib.core.database.di

import android.content.Context
import androidx.room.Room
import com.swahilib.core.database.AppDatabase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "SwahiliLibrary")
            .addMigrations(*ALL_MIGRATIONS)
            .build()

    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historiesDao()
    @Provides fun provideIdiomDao(db: AppDatabase): IdiomDao = db.idiomsDao()
    @Provides fun provideProverbDao(db: AppDatabase): ProverbDao = db.proverbsDao()
    @Provides fun provideSayingDao(db: AppDatabase): SayingDao = db.sayingsDao()
    @Provides fun provideSearchDao(db: AppDatabase): SearchDao = db.searchesDao()
    @Provides fun provideWordDao(db: AppDatabase): WordDao = db.wordsDao()
    @Provides fun provideDailyContentDao(db: AppDatabase): DailyContentDao = db.dailyContentDao()

    @Provides fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()
    @Provides fun provideXpEventDao(db: AppDatabase): XpEventDao = db.xpEventDao()
    @Provides fun provideAchievementRecordDao(db: AppDatabase): AchievementRecordDao = db.achievementRecordDao()
    @Provides fun provideChallengeDao(db: AppDatabase): ChallengeDao = db.challengeDao()
    @Provides fun provideDailyActivityDao(db: AppDatabase): DailyActivityDao = db.dailyActivityDao()
    @Provides fun provideLearningHistoryDao(db: AppDatabase): LearningHistoryDao = db.learningHistoryDao()
    @Provides fun provideGameProgressDao(db: AppDatabase): GameProgressDao = db.gameProgressDao()
}
