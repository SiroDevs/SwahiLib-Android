package com.swahilib.core.network.di

import com.swahilib.core.common.utils.ApiConstants
import com.swahilib.core.network.api.KamusiApi
import com.swahilib.core.network.services.PaystackService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
@Suppress("unused")
object NetworkModule {

    @Provides
    @Reusable
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        builder.addInterceptor(loggingInterceptor)

        return builder.build()
    }

    @Provides
    @Reusable
    fun providePaystackApiService(@Named("paystackApi") retrofit: Retrofit): PaystackService {
        return retrofit.create(PaystackService::class.java)
    }

    @Provides
    @Named("paystackApi")
    @Reusable
    fun providePaystackApi(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.PAYSTACK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideKamusiApi(client: OkHttpClient): KamusiApi = KamusiApi(client)
}