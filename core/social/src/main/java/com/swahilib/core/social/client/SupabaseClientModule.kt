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
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
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
        // Our DTOs intentionally don't model every column (e.g. competitions.created_at,
        // scores.updated_at) - without this, decoding a plain `select()` (which returns every
        // column) would throw on any field we didn't bother mapping.
        defaultSerializer = KotlinXSerializer(Json { ignoreUnknownKeys = true })
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}
