package com.swahilib.core.data.di

import android.content.Context
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.daos.HistoryDao
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.SayingDao
import com.swahilib.core.database.daos.SearchDao
import com.swahilib.core.database.daos.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {
    @Provides
    @Singleton
    fun providePrefsRepo(@ApplicationContext ctx: Context): PrefsRepo =
        PrefsRepo(ctx)

    @Provides
    @Singleton
    fun provideWordRepo(wordDao: WordDao, supabase: Postgrest): WordRepo =
        WordRepo(wordDao, supabase)

    @Provides
    @Singleton
    fun provideIdiomRepo(idiomDao: IdiomDao, supabase: Postgrest): IdiomRepo =
        IdiomRepo(idiomDao, supabase)

    @Provides
    @Singleton
    fun provideProverbRepo(proverbDao: ProverbDao, supabase: Postgrest): ProverbRepo =
        ProverbRepo(proverbDao, supabase)

    @Provides
    @Singleton
    fun provideSayingRepo(sayingDao: SayingDao, supabase: Postgrest): SayingRepo =
        SayingRepo(sayingDao, supabase)

    @Provides
    @Singleton
    fun provideHistoryRepo(historyDao: HistoryDao, supabase: Postgrest): HistoryRepo =
        HistoryRepo(historyDao, supabase)

    @Provides
    @Singleton
    fun provideSearchRepo(searchDao: SearchDao, supabase: Postgrest): SearchRepo =
        SearchRepo(searchDao, supabase)
}
