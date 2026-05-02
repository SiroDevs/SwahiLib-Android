package com.swahilib.data.di

import com.swahilib.data.sources.local.AppDatabase
import com.swahilib.data.sources.local.daos.HistoryDao
import com.swahilib.data.sources.local.daos.IdiomDao
import com.swahilib.data.sources.local.daos.ProverbDao
import com.swahilib.data.sources.local.daos.SayingDao
import com.swahilib.data.sources.local.daos.SearchDao
import com.swahilib.data.sources.local.daos.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()

    @Provides
    fun provideIdiomDao(db: AppDatabase): IdiomDao = db.idiomDao()

    @Provides
    fun provideProverbDao(db: AppDatabase): ProverbDao = db.proverbDao()

    @Provides
    fun provideSayingDao(db: AppDatabase): SayingDao = db.sayingDao()

    @Provides
    fun provideSearchDao(db: AppDatabase): SearchDao = db.searchDao()

    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()
}
