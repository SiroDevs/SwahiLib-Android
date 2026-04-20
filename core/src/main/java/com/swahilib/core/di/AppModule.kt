package com.swahilib.core.di

import android.content.*
import com.swahilib.domain.repos.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [DatabaseModule::class, SupabaseModule::class])
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