package com.swahilib.app.di

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
    @Named("paystack_secret")
    fun providePaystackSecret(): String = BuildConfig.PaystackSecret
}
