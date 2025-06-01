package com.swahilib.core.di

import com.swahilib.BuildConfig
import com.swahilib.core.helpers.Supabase
import com.swahilib.core.utils.ApiConstants
import com.swahilib.data.sources.remote.ApiService
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
@Suppress("unused")
object NetworkModule {

    @Provides
    @Reusable
    @JvmStatic
    fun provideApiService(@Named("swahilibApi") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Named("swahilibApi")
    @Reusable
    @JvmStatic
    fun provideSwahilibApi(
        okHttpClient: OkHttpClient.Builder,
        supabase: Supabase,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideOkHttp(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(logging)
        }
        return okHttpClient
    }
}