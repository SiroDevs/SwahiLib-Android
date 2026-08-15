package com.swahilib.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.navigation.graphs.contentGraph
import com.swahilib.navigation.graphs.donationGraph
import com.swahilib.navigation.graphs.gamesGraph
import com.swahilib.navigation.graphs.progressGraph
import com.swahilib.navigation.graphs.settingsGraph
import com.swahilib.navigation.graphs.socialGraph

/**
 * Top-level nav graph. Route registration is split by feature area into
 * `com.swahilib.navigation.graphs` - see [contentGraph], [settingsGraph],
 * [socialGraph], [donationGraph], [progressGraph], and [gamesGraph] - so
 * this file stays a thin orchestrator rather than a single growing list.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    themeRepo: ThemeRepo,
    prefsRepo: PrefsRepo,
    deepLinkRoute: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        contentGraph(
            navController = navController,
            prefsRepo = prefsRepo,
            deepLinkRoute = deepLinkRoute,
            onDeepLinkConsumed = onDeepLinkConsumed,
        )
        settingsGraph(navController = navController, themeRepo = themeRepo)
        socialGraph(navController = navController)
        donationGraph(navController = navController, prefsRepo = prefsRepo)
        progressGraph(navController = navController)
        gamesGraph(navController = navController)
    }
}
