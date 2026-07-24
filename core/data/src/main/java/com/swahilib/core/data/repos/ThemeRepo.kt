package com.swahilib.core.data.repos

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@HiltViewModel
class ThemeRepo @Inject constructor(
    private val prefs: PrefsRepo,
) : ViewModel() {

    var selectedTheme by mutableStateOf(prefs.appThemeMode)
        private set

    fun setTheme(mode: ThemeMode) {
        prefs.appThemeMode = mode
        selectedTheme = mode
    }
}
