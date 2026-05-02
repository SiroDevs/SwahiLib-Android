package com.swahilib.di

import android.content.Context
import com.swahilib.domain.repos.HistoryRepo
import com.swahilib.domain.repos.IdiomRepo
import com.swahilib.domain.repos.PrefsRepo
import com.swahilib.domain.repos.ProverbRepo
import com.swahilib.domain.repos.SayingRepo
import com.swahilib.domain.repos.SearchRepo
import com.swahilib.domain.repos.SubsRepo
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
        @ApplicationContext context: Context,
        supabase: Postgrest,
    ): HistoryRepo = HistoryRepo(context, supabase)

    @Provides
    @Singleton
    fun provideIdiomRepo(
        @ApplicationContext context: Context,
        supabase: Postgrest,
    ): IdiomRepo = IdiomRepo(context, supabase)

    @Provides
    @Singleton
    fun provideProverbRepo(
        @ApplicationContext context: Context,
        supabase: Postgrest,
    ): ProverbRepo = ProverbRepo(context, supabase)

    @Provides
    @Singleton
    fun provideSayingRepo(
        @ApplicationContext context: Context,
        supabase: Postgrest,
    ): SayingRepo = SayingRepo(context, supabase)

    @Provides
    @Singleton
    fun provideSearchRepo(
        @ApplicationContext context: Context,
        supabase: Postgrest,
    ): SearchRepo = SearchRepo(context, supabase)

    @Provides
    @Singleton
    fun provideSubsRepo(): SubsRepo = SubsRepo()

    @Provides
    @Singleton
    fun provideWordRepo(
        @ApplicationContext context: Context,
        supabase: Postgrest,
    ): WordRepo = WordRepo(context, supabase)

}