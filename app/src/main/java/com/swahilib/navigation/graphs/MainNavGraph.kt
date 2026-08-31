package com.swahilib.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.database.entities.content.WordEntity
import com.swahilib.feature.advanced_search.view.screen.AdvancedSearchScreen
import com.swahilib.feature.advanced_search.viewmodel.AdvancedSearchViewModel
import com.swahilib.feature.daily_content.view.screens.DailyContentHistory
import com.swahilib.feature.daily_content.view.screens.DailyProverbScreen
import com.swahilib.feature.daily_content.view.screens.DailyWordScreen
import com.swahilib.feature.history.view.HistoryScreen
import com.swahilib.feature.home.view.screen.HomeScreen
import com.swahilib.feature.home.viewmodel.HomeViewModel
import com.swahilib.feature.idiom.view.IdiomScreen
import com.swahilib.feature.idiom.viewmodel.IdiomViewModel
import com.swahilib.feature.library.view.screens.LibraryScreen
import com.swahilib.feature.library.viewmodel.LibraryViewModel
import com.swahilib.feature.likes.view.LikesScreen
import com.swahilib.feature.proverb.view.screen.ProverbScreen
import com.swahilib.feature.proverb.viewmodel.ProverbViewModel
import com.swahilib.feature.saying.view.SayingScreen
import com.swahilib.feature.saying.viewmodel.SayingViewModel
import com.swahilib.feature.word.view.WordScreen
import com.swahilib.feature.word.viewmodel.WordViewModel

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    deepLinkRoute: String?,
    onDeepLinkConsumed: () -> Unit,
) {
    composable(Routes.HOME) {
        val viewModel: HomeViewModel = hiltViewModel()
        HomeScreen(
            viewModel = viewModel,
            navController = navController,
            prefsRepo = prefsRepo,
            deepLinkRoute = deepLinkRoute,
            onDeepLinkConsumed = onDeepLinkConsumed
        )
    }

    composable(
        route = Routes.LIBRARY_COLLECTION,
        arguments = listOf(
            navArgument("collectionKey") { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        val collectionKey = backStackEntry.arguments?.getString("collectionKey") ?: return@composable
        val viewModel: LibraryViewModel = hiltViewModel()
        LibraryScreen(
            navController = navController,
            viewModel = viewModel,
            collectionKey = collectionKey,
        )
    }

    composable(Routes.IDIOM) {
        val idiom =
            navController.previousBackStackEntry?.savedStateHandle?.get<IdiomEntity>("idiom")
        val viewModel: IdiomViewModel = hiltViewModel()
        IdiomScreen(
            navController = navController,
            viewModel = viewModel,
            idiom = idiom,
            prefsRepo = prefsRepo
        )
    }

    composable(Routes.PROVERB) {
        val proverb =
            navController.previousBackStackEntry?.savedStateHandle?.get<ProverbEntity>("proverb")
        val viewModel: ProverbViewModel = hiltViewModel()
        ProverbScreen(
            navController = navController,
            viewModel = viewModel,
            proverb = proverb,
            prefsRepo = prefsRepo
        )
    }

    composable(Routes.SAYING) {
        val saying =
            navController.previousBackStackEntry?.savedStateHandle?.get<SayingEntity>("saying")
        val viewModel: SayingViewModel = hiltViewModel()
        SayingScreen(
            navController = navController,
            viewModel = viewModel,
            saying = saying,
            prefsRepo = prefsRepo
        )
    }

    composable(Routes.WORD) {
        val word =
            navController.previousBackStackEntry?.savedStateHandle?.get<WordEntity>("word")
        val viewModel: WordViewModel = hiltViewModel()
        WordScreen(
            navController = navController,
            viewModel = viewModel,
            word = word,
            prefsRepo = prefsRepo
        )
    }

    composable(Routes.ADVANCED_SEARCH) {
        val viewModel: AdvancedSearchViewModel = hiltViewModel()
        AdvancedSearchScreen(
            navController = navController,
            viewModel = viewModel,
            prefsRepo = prefsRepo
        )
    }
    composable(Routes.LIKES) {
        LikesScreen(navController = navController)
    }

    composable(Routes.HISTORY) {
        HistoryScreen(navController = navController)
    }

    composable(Routes.DAILY_WORD) {
        DailyWordScreen(navController = navController, prefsRepo = prefsRepo)
    }

    composable(Routes.DAILY_PROVERB) {
        DailyProverbScreen(navController = navController, prefsRepo = prefsRepo)
    }

    composable(
        route = Routes.DAILY_CONTENT_HISTORY,
        arguments = listOf(
            navArgument("type") { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        val type = backStackEntry.arguments?.getString("type")
            ?: Routes.DAILY_CONTENT_TYPE_WORD
        DailyContentHistory(navController = navController, type = type)
    }
}
