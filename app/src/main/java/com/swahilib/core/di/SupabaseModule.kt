package com.swahilib.core.di

import com.swahilib.BuildConfig
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.*
import io.github.jan.supabase.postgrest.*
import javax.inject.*

@InstallIn(SingletonComponent::class)
@Module
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SupabaseUrl,
            supabaseKey = BuildConfig.SupabaseKey,
        ) {
            install(Postgrest)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseDatabase(client: SupabaseClient): Postgrest {
        return client.postgrest
    }
}
