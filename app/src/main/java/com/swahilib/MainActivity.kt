package com.swahilib

import android.os.*
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.annotation.*
import androidx.compose.foundation.*
import androidx.compose.ui.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.swahilib.domain.repository.*
import com.swahilib.presentation.navigation.*
import com.swahilib.presentation.theme.*
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Keep
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeRepo: ThemeRepository = hiltViewModel()
            val themeMode = themeRepo.selectedTheme
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            AppTheme(useDarkTheme = isDarkTheme) {
                AppNavHost(themeRepo = themeRepo)
            }
        }
    }
}