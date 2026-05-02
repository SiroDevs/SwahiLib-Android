package com.swahilib.di

import android.content.Context
import com.swahilib.data.sources.local.daos.HistoryDao
import com.swahilib.data.sources.local.daos.IdiomDao
import com.swahilib.data.sources.local.daos.ProverbDao
import com.swahilib.data.sources.local.daos.SayingDao
import com.swahilib.data.sources.local.daos.SearchDao
import com.swahilib.data.sources.local.daos.WordDao
import com.swahilib.domain.repos.HistoryRepo
import com.swahilib.domain.repos.IdiomRepo
import com.swahilib.core.repos.PrefsRepo
import com.swahilib.domain.repos.ProverbRepo
import com.swahilib.domain.repos.SayingRepo
import com.swahilib.domain.repos.SearchRepo
import com.swahilib.core.repos.SubsRepo
import com.swahilib.domain.repos.WordRepo
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun providePrefsRepo(
        @ApplicationContext context: Context,
    ): PrefsRepo = PrefsRepo(context)

    @Provides
    @Singleton
    fun provideHistoryRepo(
        supabase: Postgrest,
        historyDao: HistoryDao,
    ): HistoryRepo = HistoryRepo(supabase, historyDao)

    @Provides
    @Singleton
    fun provideIdiomRepo(
        supabase: Postgrest,
        idiomDao: IdiomDao,
    ): IdiomRepo = IdiomRepo(supabase, idiomDao)

    @Provides
    @Singleton
    fun provideProverbRepo(
        supabase: Postgrest,
        proverbDao: ProverbDao,
    ): ProverbRepo = ProverbRepo(supabase, proverbDao)

    @Provides
    @Singleton
    fun provideSayingRepo(
        supabase: Postgrest,
        sayingDao: SayingDao,
    ): SayingRepo = SayingRepo(supabase, sayingDao)

    @Provides
    @Singleton
    fun provideSearchRepo(
        supabase: Postgrest,
        searchDao: SearchDao,
    ): SearchRepo = SearchRepo(supabase, searchDao)

    @Provides
    @Singleton
    fun provideSubsRepo(): SubsRepo = SubsRepo()

    @Provides
    @Singleton
    fun provideWordRepo(
        supabase: Postgrest,
        wordDao: WordDao,
    ): WordRepo = WordRepo(supabase, wordDao)
}