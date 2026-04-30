package com.swahilib.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.swahilib.data.models.*
import com.swahilib.presentation.home.view.HomeScreen
import com.swahilib.presentation.init.view.InitScreen
import com.swahilib.presentation.settings.view.SettingsScreen
import com.swahilib.presentation.splash.view.SplashScreen
import com.swahilib.presentation.viewer.idiom.view.IdiomScreen
import com.swahilib.presentation.viewer.proverb.view.ProverbScreen
import com.swahilib.presentation.viewer.saying.view.SayingScreen
import com.swahilib.presentation.viewer.word.view.WordScreen
import com.swahilib.domain.repos.ThemeRepository
import com.swahilib.presentation.home.HomeViewModel
import com.swahilib.presentation.init.InitViewModel
import com.swahilib.presentation.settings.SettingsViewModel
import com.swahilib.presentation.splash.SplashViewModel
import com.swahilib.presentation.viewer.idiom.IdiomViewModel
import com.swahilib.presentation.viewer.proverb.ProverbViewModel
import com.swahilib.presentation.viewer.saying.SayingViewModel
import com.swahilib.presentation.viewer.word.WordViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    themeRepo: ThemeRepository
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = viewModel)
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

        composable(route = Routes.IDIOM) {
            val idiom = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Idiom>("idiom")

            val viewModel: IdiomViewModel = hiltViewModel()

            IdiomScreen(
                navController = navController,
                viewModel = viewModel,
                idiom = idiom,
            )
        }

        composable(route = Routes.PROVERB) {
            val proverb = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Proverb>("proverb")

            val viewModel: ProverbViewModel = hiltViewModel()

            ProverbScreen(
                navController = navController,
                viewModel = viewModel,
                proverb = proverb,
            )
        }

        composable(route = Routes.SAYING) {
            val saying = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Saying>("saying")

            val viewModel: SayingViewModel = hiltViewModel()

            SayingScreen(
                navController = navController,
                viewModel = viewModel,
                saying = saying,
            )
        }

        composable(route = Routes.WORD) {
            val word = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Word>("word")

            val viewModel: WordViewModel = hiltViewModel()

            WordScreen(
                navController = navController,
                viewModel = viewModel,
                word = word,
            )
        }

        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                navController = navController,
                viewModel = viewModel,
                themeRepo = themeRepo,
            )
        }

    }
}