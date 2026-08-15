package com.swahilib.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.data.repos.utils.ThemeRepo
import com.swahilib.navigation.graphs.gamesGraph
import com.swahilib.navigation.graphs.mainGraph
import com.swahilib.navigation.graphs.miscGraph
import com.swahilib.navigation.graphs.progressGraph
import com.swahilib.navigation.graphs.settingsGraph

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
        mainGraph(
            navController = navController,
            prefsRepo = prefsRepo,
            deepLinkRoute = deepLinkRoute,
            onDeepLinkConsumed = onDeepLinkConsumed,
        )
        settingsGraph(navController = navController, themeRepo = themeRepo)
        progressGraph(navController = navController)
        gamesGraph(navController = navController)
        miscGraph(navController = navController, prefsRepo = prefsRepo)
    }
}
