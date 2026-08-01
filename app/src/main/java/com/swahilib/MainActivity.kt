package com.swahilib

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.swahilib.app.navigation.AppNavHost
import com.swahilib.core.common.utils.DeepLinkConstants
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.data.repos.ThemeMode
import com.swahilib.core.design_system.theme.AppTheme
import com.swahilib.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsRepo: PrefsRepo

    private var deepLinkRouteState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels()

        splashScreen.setKeepOnScreenCondition {
            !mainViewModel.isReady.value
        }

        deepLinkRouteState.value = intent?.getStringExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO)

        setContent {
            val themeRepo: ThemeRepo = hiltViewModel()

            val themeMode = themeRepo.selectedTheme
            val isDarkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val deepLinkRoute by deepLinkRouteState

            AppTheme(useDarkTheme = isDarkTheme) {
                AppNavHost(
                    themeRepo = themeRepo,
                    prefsRepo = prefsRepo,
                    deepLinkRoute = deepLinkRoute,
                    onDeepLinkConsumed = { deepLinkRouteState.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkRouteState.value = intent.getStringExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO)
    }
}
