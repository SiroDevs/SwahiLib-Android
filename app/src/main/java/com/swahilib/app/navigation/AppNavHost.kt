package com.swahilib.app.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.feature.advsearch.AdvSearchViewModel
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.settings.SettingsViewModel
import com.swahilib.feature.splash.SplashViewModel
import com.swahilib.feature.idiom.IdiomViewModel
import com.swahilib.feature.proverb.ProverbViewModel
import com.swahilib.feature.saying.SayingViewModel
import com.swahilib.feature.word.WordViewModel
import com.swahilib.feature.donation.DonationViewModel
import com.swahilib.feature.home.view.HomeScreen
import com.swahilib.feature.advsearch.view.AdvSearchScreen
import com.swahilib.feature.splash.view.SplashScreen
import com.swahilib.feature.idiom.view.IdiomScreen
import com.swahilib.feature.proverb.view.ProverbScreen
import com.swahilib.feature.saying.view.SayingScreen
import com.swahilib.feature.word.view.WordScreen
import com.swahilib.feature.settings.view.SettingsScreen
import com.swahilib.feature.howitworks.view.HowItWorksScreen
import com.swahilib.feature.help.view.HelpScreen
import com.swahilib.feature.donation.view.DonationScreen
import com.swahilib.feature.dailyword.view.DailyWordScreen
import com.swahilib.feature.dailyword.view.DailyProverbScreen

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    themeRepo: ThemeRepo,
    prefsRepo: PrefsRepo,
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            val viewModel: SplashViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.INIT) {
            LaunchedEffect(Unit) {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.INIT) { inclusive = true }
                }
            }
        }

        composable(Routes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(viewModel = viewModel, navController = navController, prefsRepo = prefsRepo)
        }

        composable(Routes.IDIOM) {
            val idiom = navController.previousBackStackEntry?.savedStateHandle?.get<IdiomEntity>("idiom")
            val viewModel: IdiomViewModel = hiltViewModel()
            IdiomScreen(navController = navController, viewModel = viewModel, idiom = idiom, prefsRepo = prefsRepo)
        }

        composable(Routes.PROVERB) {
            val proverb = navController.previousBackStackEntry?.savedStateHandle?.get<ProverbEntity>("proverb")
            val viewModel: ProverbViewModel = hiltViewModel()
            ProverbScreen(navController = navController, viewModel = viewModel, proverb = proverb, prefsRepo = prefsRepo)
        }

        composable(Routes.SAYING) {
            val saying = navController.previousBackStackEntry?.savedStateHandle?.get<SayingEntity>("saying")
            val viewModel: SayingViewModel = hiltViewModel()
            SayingScreen(navController = navController, viewModel = viewModel, saying = saying, prefsRepo = prefsRepo)
        }

        composable(Routes.WORD) {
            val word = navController.previousBackStackEntry?.savedStateHandle?.get<WordEntity>("word")
            val viewModel: WordViewModel = hiltViewModel()
            WordScreen(navController = navController, viewModel = viewModel, word = word, prefsRepo = prefsRepo)
        }

        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(navController = navController, viewModel = viewModel, themeRepo = themeRepo)
        }

        composable(Routes.ADVSEARCH) {
            val viewModel: AdvSearchViewModel = hiltViewModel()
            AdvSearchScreen(navController = navController, viewModel = viewModel, prefsRepo = prefsRepo)
        }

        composable(Routes.HOW_IT_WORKS) { HowItWorksScreen(navController = navController) }
        composable(Routes.HELP) { HelpScreen(navController = navController) }

        composable(Routes.DONATION) {
            val viewModel: DonationViewModel = hiltViewModel()
            DonationScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.DAILY_WORD) {
            DailyWordScreen(navController = navController, prefsRepo = prefsRepo)
        }

        composable(Routes.DAILY_PROVERB) {
            DailyProverbScreen(navController = navController, prefsRepo = prefsRepo)
        }
    }
}
