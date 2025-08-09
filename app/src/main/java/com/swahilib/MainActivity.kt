package com.swahilib

import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.annotation.Keep
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.swahilib.presentation.navigation.*
import com.swahilib.presentation.theme.AppTheme
import com.swahilib.presentation.theme.SwahiliLibTheme
import com.swahilib.presentation.theme.ThemeManager
import com.swahilib.presentation.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val themeManager: ThemeManager = hiltViewModel()
            val themeMode = themeManager.selectedTheme
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            AppTheme(useDarkTheme = isDarkTheme) {
                AppNavHost(themeManager = themeManager)
            }
        }
    }
}