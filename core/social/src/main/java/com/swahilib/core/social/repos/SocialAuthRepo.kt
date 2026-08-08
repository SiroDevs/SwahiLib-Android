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

@Singleton
class SocialAuthRepo @Inject constructor(
    private val supabase: SupabaseClient,
) {

    val sessionStatus: Flow<SessionStatus> get() = supabase.auth.sessionStatus

    val isSignedIn: Flow<Boolean> get() = sessionStatus.map { it is SessionStatus.Authenticated }

    val currentUserId: String? get() = supabase.auth.currentUserOrNull()?.id

    suspend fun signInWithGoogle(context: Context): Result<Unit> = runCatching {
        val rawNonce = generateNonce()
        val hashedNonce = sha256Hex(rawNonce)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GoogleWebClientId)
            .setNonce(hashedNonce)
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
            nonce = rawNonce
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

    private fun sha256Hex(input: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
