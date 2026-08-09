package com.swahilib.feature.auth.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.social.model.SocialProfile
import com.swahilib.core.social.repos.SocialAuthRepo
import com.swahilib.core.social.repos.SocialRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives sign-in/out. [SocialAuthRepo] now talks to Firebase Auth rather than Supabase Auth
 * directly (Supabase trusts Firebase's JWTs via Third-Party Auth instead) - nothing here needed
 * to change as a result, since that swap is entirely internal to SocialAuthRepo.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: SocialAuthRepo,
    private val socialRepo: SocialRepo,
) : ViewModel() {

    val isSignedIn: StateFlow<Boolean> = authRepo.isSignedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _profile = MutableStateFlow<SocialProfile?>(null)
    val profile: StateFlow<SocialProfile?> = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.isSignedIn.collect { signedIn ->
                _profile.value = if (signedIn) socialRepo.currentProfile() else null
            }
        }
    }

    fun signIn(context: Context) {
        if (_isLoading.value) return
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            authRepo.signInWithGoogle(context).fold(
                onSuccess = { googleDisplayName ->
                    val name = googleDisplayName?.trim()?.takeIf { it.isNotEmpty() } ?: "Mchezaji"
                    _profile.value = socialRepo.ensureProfile(name)
                    if (_profile.value == null) {
                        _error.value = "Imeshindwa kuunda wasifu wako. Jaribu tena."
                    }
                },
                onFailure = {
                    _error.value = "Imeshindwa kuingia na Google. Jaribu tena."
                },
            )
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepo.signOut()
            _profile.value = null
        }
    }

    fun clearError() {
        _error.value = null
    }
}
