package com.swahilib.core.social.repos

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.swahilib.core.social.BuildConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles Google Sign-In (still via Credential Manager, same as before) but now exchanges the
 * resulting ID token with Firebase Auth instead of Supabase Auth directly. Firebase becomes the
 * actual identity/session provider; Supabase trusts Firebase's JWTs via Third-Party Auth (see
 * SupabaseClient.kt for the required Supabase Dashboard + Firebase custom-claim setup this
 * depends on - none of that can be done from this file).
 */
@Singleton
class SocialAuthRepo @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {

    val isSignedIn: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser != null) }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    val currentUserId: String? get() = firebaseAuth.currentUser?.uid

    suspend fun signInWithGoogle(context: Context): Result<String?> = runCatching {
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

        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        firebaseAuth.signInWithCredential(firebaseCredential).await()

        googleIdTokenCredential.displayName
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
    }

    /** Current Firebase ID token, handed to Supabase on every request - see SupabaseClient.kt. */
    suspend fun supabaseAccessToken(): String? =
        firebaseAuth.currentUser?.getIdToken(false)?.await()?.token

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
