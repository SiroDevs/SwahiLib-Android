package com.swahilib.di

import com.swahilib.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkConfigModule {
    @Provides
    @Named("supabase_url")
    fun provideSupabaseUrl(): String = BuildConfig.SupabaseUrl

    @Provides
    @Named("supabase_key")
    fun provideSupabaseKey(): String = BuildConfig.SupabaseKey
}
