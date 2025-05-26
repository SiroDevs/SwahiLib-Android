package com.swahilib

import android.os.Bundle
import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.annotation.Keep
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.swahilib.presentation.navigation.*
import com.swahilib.presentation.theme.SwahiliLibTheme
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
            SwahiliLibTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}