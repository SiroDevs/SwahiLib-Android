package com.swahilib.core.di

import android.content.Context
import com.swahilib.data.sources.remote.ApiService
import com.swahilib.domain.repositories.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [NetworkModule::class])
class AppModule {
    @Provides
    @Singleton
    fun provideWordRepository(
        @ApplicationContext context: Context,
        apiService: ApiService
    ): WordRepository = WordRepository(context, apiService)

}