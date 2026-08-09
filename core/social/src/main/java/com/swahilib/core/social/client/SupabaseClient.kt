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

/**
 * Supabase client configured for Firebase Auth as a Third-Party Auth provider
 * (see https://supabase.com/docs/guides/auth/third-party/firebase-auth), rather than using
 * Supabase's own Auth module. Firebase manages the actual sign-in/session; every request to
 * Supabase (Postgrest/Realtime) is authorized by handing over the current Firebase ID token via
 * [accessToken]. This is why `Auth` is not installed here: nothing in this app calls
 * `supabase.auth.signIn...` anymore - [SocialAuthRepo] talks to Firebase directly, and
 * `auth.uid()` in Postgres RLS policies resolves to the Firebase user's UID automatically once
 * the token is being sent this way (no RLS/schema changes needed).
 *
 * NOTE: this requires manual one-time setup outside of code that nothing here can do for you:
 * 1. In the Supabase Dashboard -> Authentication -> Third-Party Auth, add a Firebase integration
 *    pointing at your Firebase Project ID.
 * 2. Every Firebase user needs a `role: "authenticated"` custom claim (via a Firebase Auth
 *    Blocking Function or the Admin SDK) - see SocialAuthRepo.kt for why, and the Supabase docs
 *    link above for the exact Cloud Function snippet. Without this, Supabase treats every
 *    Firebase-authenticated request as the `anon` role, and RLS will silently deny everything.
 */
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
        // Supplies every Postgrest/Realtime request with the current Firebase ID token instead
        // of a Supabase-issued one. NOTE: this specific property is the one piece of this file
        // I could not verify against a live copy of the supabase-kt docs (the Kotlin code sample
        // on the Firebase Auth guide is behind a JS-rendered tab search couldn't reach) - if the
        // build fails specifically on this line, that's the first thing to check against
        // https://supabase.com/docs/guides/auth/third-party/firebase-auth (Kotlin (Android) tab).
        accessToken = { authRepo.supabaseAccessToken() }
        install(Postgrest)
        install(Realtime)
    }
}
