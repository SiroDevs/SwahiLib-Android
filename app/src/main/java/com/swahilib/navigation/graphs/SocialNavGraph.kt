package com.swahilib.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.swahilib.core.common.utils.Routes
import com.swahilib.feature.auth.view.SignInScreen
import com.swahilib.feature.history.view.HistoryScreen
import com.swahilib.feature.likes.view.LikesScreen
import com.swahilib.feature.social.view.screen.SocialScreen

/** Likes, history, sign-in, and the community/social hub. */
fun NavGraphBuilder.socialGraph(navController: NavHostController) {
    composable(Routes.LIKES) {
        LikesScreen(navController = navController)
    }

    composable(Routes.HISTORY) {
        HistoryScreen(navController = navController)
    }

    composable(Routes.AUTH_SIGN_IN) {
        SignInScreen(navController = navController)
    }

    composable(Routes.SOCIAL) {
        SocialScreen(navController = navController)
    }
}
