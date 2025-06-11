package com.swahilib.core.di

import android.content.Context
import android.content.SharedPreferences
import com.swahilib.core.utils.PrefConstants
import com.swahilib.data.sources.remote.ApiService
import com.swahilib.domain.repository.*
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
    fun provideSharePreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun provideWordRepository(
        @ApplicationContext context: Context,
        apiService: ApiService
    ): WordRepository = WordRepository(context, apiService)

    @Singleton
    @Binds
    fun bindOrderRepository(impl: WordRepositoryImpl): WordRepository

}