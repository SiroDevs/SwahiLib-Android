package com.swahilib.core.di

import android.content.Context
import androidx.room.Room
import com.swahilib.data.sources.local.AppDatabase
import com.swahilib.data.sources.local.daos.HistoryDao
import com.swahilib.data.sources.local.daos.IdiomDao
import com.swahilib.data.sources.local.daos.ProverbDao
import com.swahilib.data.sources.local.daos.SayingDao
import com.swahilib.data.sources.local.daos.SearchDao
import com.swahilib.data.sources.local.daos.WordDao
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "SwahiLib"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideHistoryDao(database: AppDatabase): HistoryDao {
        return database.historiesDao()
    }

    @Provides
    fun provideIdiomDao(database: AppDatabase): IdiomDao {
        return database.idiomsDao()
    }

    @Provides
    fun provideProverbDao(database: AppDatabase): ProverbDao {
        return database.proverbsDao()
    }

    @Provides
    fun provideSayingDao(database: AppDatabase): SayingDao {
        return database.sayingsDao()
    }

    @Provides
    fun provideSearchDao(database: AppDatabase): SearchDao {
        return database.searchesDao()
    }

    @Provides
    fun provideWordDao(database: AppDatabase): WordDao {
        return database.wordsDao()
    }
}
