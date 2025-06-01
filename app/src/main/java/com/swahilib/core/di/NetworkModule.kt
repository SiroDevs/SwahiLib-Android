package com.swahilib.core.di

import com.swahilib.BuildConfig
import com.swahilib.core.helpers.MySupabase
import com.swahilib.core.utils.ApiConstants
import com.swahilib.data.sources.remote.ApiService
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.*

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    @Reusable
    fun provideApiService(@Named("swahilibApi") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Named("swahilibApi")
    @Reusable
    fun provideSwahilibApi(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Reusable
    fun provideOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(logging)
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = MySupabase.client

}
