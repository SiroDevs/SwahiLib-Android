package com.swahilib.di

import com.swahilib.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {
    @Provides
    @Named("supabase_url")
    fun provideSupabaseUrl(): String = BuildConfig.SupabaseUrl

    @Provides
    @Named("supabase_key")
    fun provideSupabaseKey(): String = BuildConfig.SupabaseKey

    @Provides
    @Named("pesapal_consumer_key")
    fun providePesapalConsumerKey(): String = BuildConfig.PesapalConsumerKey

    @Provides
    @Named("pesapal_consumer_secret")
    fun providePesapalConsumerSectret(): String = BuildConfig.PesapalConsumerSecret

    @Provides
    @Named("pesapal_ipn_id")
    fun providePesapalIpnId(): String = BuildConfig.PesapalIpnId
}
