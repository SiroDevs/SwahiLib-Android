package com.swahilib.presentation.screens.splash

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swahilib.domain.repository.PrefsRepository
import com.swahilib.presentation.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { PrefsRepository(context) }

    LaunchedEffect(Unit) {
        delay(3000)

        val nextRoute = when {
            prefs.isDataLoaded -> Routes.HOME
            else -> Routes.INIT
        }

        navController.navigate(nextRoute) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    SplashContent()
}
