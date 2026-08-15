package com.swahilib.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.swahilib.core.common.utils.Routes
import com.swahilib.feature.progress.view.screen.AchievementsScreen
import com.swahilib.feature.progress.view.screen.ChallengesScreen
import com.swahilib.feature.progress.view.screen.ProgressScreen
import com.swahilib.feature.progress.view.screen.StatisticsScreen
import com.swahilib.feature.progress.viewmodel.ProgressViewModel

/** Progress overview, statistics, achievements, and challenges. */
fun NavGraphBuilder.progressGraph(navController: NavHostController) {
    composable(Routes.PROGRESS) {
        val viewModel: ProgressViewModel = hiltViewModel()
        ProgressScreen(navController = navController, viewModel = viewModel)
    }

    composable(Routes.STATISTICS) {
        val viewModel: ProgressViewModel = hiltViewModel()
        StatisticsScreen(navController = navController, viewModel = viewModel)
    }

    composable(Routes.ACHIEVEMENTS) {
        val viewModel: ProgressViewModel = hiltViewModel()
        AchievementsScreen(navController = navController, viewModel = viewModel)
    }

    composable(Routes.CHALLENGES) {
        val viewModel: ProgressViewModel = hiltViewModel()
        ChallengesScreen(navController = navController, viewModel = viewModel)
    }
}
