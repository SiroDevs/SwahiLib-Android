package com.swahilib.core.di

import android.content.Context
import android.content.SharedPreferences
import com.swahilib.core.utils.PrefConstants
import com.swahilib.domain.repository.WordRepository
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
    fun provideSharePreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideWordRepository(
        @ApplicationContext context: Context,
        supabase: Postgrest,
    ): WordRepository = WordRepository(context, supabase)

}