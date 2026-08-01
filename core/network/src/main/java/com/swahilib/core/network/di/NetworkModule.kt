package com.swahilib.core.network.di

import com.swahilib.core.common.utils.ApiConstants
import com.swahilib.core.network.api.KamusiApi
import com.swahilib.core.network.services.DonationApiService
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
    fun provideDonationApiService(@Named("donationApi") retrofit: Retrofit): DonationApiService {
        return retrofit.create(DonationApiService::class.java)
    }

    @Provides
    @Named("donationApi")
    @Reusable
    fun provideDonationApi(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.DONATION_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideKamusiApi(client: OkHttpClient): KamusiApi = KamusiApi(client)
}