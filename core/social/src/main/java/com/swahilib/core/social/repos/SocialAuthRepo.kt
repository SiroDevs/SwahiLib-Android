package com.swahilib.core.social.repos

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.swahilib.core.social.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles account creation/sign-in via Google (through Credential Manager,
 * matching the pattern already used for SongLib), exchanged with Supabase
 * Auth. Community features are entirely opt-in - the rest of the app works
 * fully offline without ever calling this.
 *
 * NOTE: written against supabase-kt's auth-kt API surface from memory (no
 * network access to verify against current docs in this environment) -
 * double check `IDToken`/`Google` provider usage against the installed
 * version before relying on it.
 */
@Singleton
class SocialAuthRepo @Inject constructor(
    private val supabase: SupabaseClient,
) {

    val sessionStatus: Flow<SessionStatus> get() = supabase.auth.sessionStatus

    val isSignedIn: Flow<Boolean> get() = sessionStatus.map { it is SessionStatus.Authenticated }

    val currentUserId: String? get() = supabase.auth.currentUserOrNull()?.id

    /**
     * Launches the Credential Manager Google Sign-In flow and exchanges the
     * resulting ID token with Supabase Auth. Returns true on success.
     */
    suspend fun signInWithGoogle(context: Context): Result<Unit> = runCatching {
        val nonce = generateNonce()
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(context)
        val result = try {
            credentialManager.getCredential(context, request)
        } catch (e: GetCredentialException) {
            throw IllegalStateException("Google sign-in was cancelled or failed", e)
        }

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)

        supabase.auth.signInWith(IDToken) {
            idToken = googleIdTokenCredential.idToken
            provider = Google
//            nonce = nonce
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    private fun generateNonce(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) } + UUID.randomUUID().toString().take(8)
    }
}
