package com.swahilib.core.social.client

import com.swahilib.core.social.BuildConfig
import com.swahilib.core.social.repos.SocialAuthRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseClientModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(authRepo: SocialAuthRepo): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SupabaseKey,
        supabaseKey = BuildConfig.SupabaseAnonKey,
    ) {
        defaultSerializer = KotlinXSerializer(Json { ignoreUnknownKeys = true })
        accessToken = { authRepo.supabaseAccessToken() }
        install(Postgrest)
        install(Realtime)
    }
}
