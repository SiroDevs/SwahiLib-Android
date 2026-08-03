package com.swahilib.core.social.client

import com.swahilib.core.social.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import javax.inject.Singleton

/**
 * Single [SupabaseClient] for the whole app. Requires SUPABASE_URL and
 * SUPABASE_ANON_KEY in local.properties (see local.properties.example and
 * docs/supabase_schema.sql) - without them, every social feature degrades
 * to its "signed out" / empty state rather than crashing, since the anon
 * key alone can't authenticate real requests anyway.
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseClientModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}
