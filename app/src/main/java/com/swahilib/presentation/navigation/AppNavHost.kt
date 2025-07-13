package com.swahilib.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.swahilib.data.models.Word
import com.swahilib.presentation.screens.home.HomeScreen
import com.swahilib.presentation.screens.init.InitScreen
import com.swahilib.presentation.screens.splash.SplashScreen
import com.swahilib.presentation.screens.word.WordViewer
import com.swahilib.presentation.viewmodels.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(Routes.INIT) {
            val viewModel: InitViewModel = hiltViewModel()
            InitScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable(Routes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                navController = navController,
            )
        }

        composable(route = Routes.WORD) {
            val word = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Word>("word")

            val viewModel: WordViewModel = hiltViewModel()

            WordViewer(
                onBackPressed = { navController.popBackStack() },
                viewModel = viewModel,
                word = word,
            )
        }
    }
}