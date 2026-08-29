package com.swahilib.core.database.di

import android.content.Context
import androidx.room.Room
import com.swahilib.core.database.AppDatabase
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
import com.swahilib.core.database.daos.library.CapsDao
import com.swahilib.core.database.daos.library.CountriesDao
import com.swahilib.core.database.daos.library.FamilyDao
import com.swahilib.core.database.daos.library.FishDao
import com.swahilib.core.database.daos.library.GreetingsDao
import com.swahilib.core.database.daos.library.InsectsDao
import com.swahilib.core.database.daos.library.KidGamesDao
import com.swahilib.core.database.daos.library.PunctuationDao
import com.swahilib.core.database.daos.library.SeasDao
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

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historiesDao()
    @Provides
    fun provideIdiomDao(db: AppDatabase): IdiomDao = db.idiomsDao()
    @Provides
    fun provideProverbDao(db: AppDatabase): ProverbDao = db.proverbsDao()
    @Provides
    fun provideSayingDao(db: AppDatabase): SayingDao = db.sayingsDao()
    @Provides
    fun provideSearchDao(db: AppDatabase): SearchDao = db.searchesDao()
    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordsDao()
    @Provides
    fun provideDailyContentDao(db: AppDatabase): DailyContentDao = db.dailyContentDao()

    @Provides
    fun provideUserProgressDao(db: AppDatabase): UserProgressDao = db.userProgressDao()
    @Provides
    fun provideXpEventDao(db: AppDatabase): XpEventDao = db.xpEventDao()
    @Provides
    fun provideAchievementRecordDao(db: AppDatabase): AchievementRecordDao =
        db.achievementRecordDao()

    @Provides
    fun provideChallengeDao(db: AppDatabase): ChallengeDao = db.challengeDao()
    @Provides
    fun provideDailyActivityDao(db: AppDatabase): DailyActivityDao = db.dailyActivityDao()
    @Provides
    fun provideLearningHistoryDao(db: AppDatabase): LearningHistoryDao = db.learningHistoryDao()
    @Provides
    fun provideGameProgressDao(db: AppDatabase): GameProgressDao = db.gameProgressDao()

    @Provides fun provideFamilyDao(db: AppDatabase): FamilyDao = db.familyDao()
    @Provides fun provideCapsDao(db: AppDatabase): CapsDao = db.capsDao()
    @Provides fun provideFishDao(db: AppDatabase): FishDao = db.fishDao()
    @Provides fun provideInsectsDao(db: AppDatabase): InsectsDao = db.insectsDao()
    @Provides fun provideSeasDao(db: AppDatabase): SeasDao = db.seasDao()
    @Provides fun provideKidGamesDao(db: AppDatabase): KidGamesDao = db.kidGamesDao()
    @Provides fun provideGreetingsDao(db: AppDatabase): GreetingsDao = db.greetingsDao()
    @Provides fun provideCountriesDao(db: AppDatabase): CountriesDao = db.countriesDao()
    @Provides fun providePunctuationDao(db: AppDatabase): PunctuationDao = db.punctuationDao()
}
