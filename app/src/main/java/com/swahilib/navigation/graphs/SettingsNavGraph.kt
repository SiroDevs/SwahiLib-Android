package com.swahilib.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.feature.settings.view.screen.AppearanceSettingsScreen
import com.swahilib.feature.settings.view.screen.DataSettingsScreen
import com.swahilib.feature.settings.view.screen.GameSettingsScreen
import com.swahilib.feature.settings.view.screen.NotificationSettingsScreen
import com.swahilib.feature.settings.view.screen.SettingsScreen
import com.swahilib.feature.settings.viewmodel.GameSettingsViewModel
import com.swahilib.feature.settings.viewmodel.SettingsViewModel

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    themeRepo: ThemeRepo,
) {
    composable(Routes.SETTINGS) {
        SettingsScreen(navController = navController)
    }

    composable(Routes.SETTINGS_APPEARANCE) {
        AppearanceSettingsScreen(navController = navController, themeRepo = themeRepo)
    }

    composable(Routes.SETTINGS_NOTIFICATIONS) {
        val viewModel: SettingsViewModel = hiltViewModel()
        NotificationSettingsScreen(navController = navController, viewModel = viewModel)
    }

    composable(Routes.SETTINGS_DATA) {
        DataSettingsScreen(navController = navController)
    }

    composable(Routes.SETTINGS_GAMES) {
        val viewModel: GameSettingsViewModel = hiltViewModel()
        GameSettingsScreen(navController = navController, viewModel = viewModel)
    }
}
