package com.swahilib.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.feature.advanced_search.viewmodel.AdvSearchViewModel
import com.swahilib.feature.home.viewmodel.HomeViewModel
import com.swahilib.feature.settings.viewmodel.SettingsViewModel
import com.swahilib.feature.idiom.viewmodel.IdiomViewModel
import com.swahilib.feature.proverb.viewmodel.ProverbViewModel
import com.swahilib.feature.saying.viewmodel.SayingViewModel
import com.swahilib.feature.word.viewmodel.WordViewModel
import com.swahilib.feature.donation.viewmodel.DonationViewModel
import com.swahilib.feature.home.view.screen.HomeScreen
import com.swahilib.feature.advanced_search.view.screen.AdvSearchScreen
import com.swahilib.feature.daily_content.view.DailyContentHistory
import com.swahilib.feature.idiom.view.IdiomScreen
import com.swahilib.feature.proverb.view.screen.ProverbScreen
import com.swahilib.feature.saying.view.SayingScreen
import com.swahilib.feature.word.view.WordScreen
import com.swahilib.feature.settings.view.screen.SettingsScreen
import com.swahilib.feature.settings.view.screen.AppearanceSettingsScreen
import com.swahilib.feature.settings.view.screen.NotificationSettingsScreen
import com.swahilib.feature.settings.view.screen.DataSettingsScreen
import com.swahilib.feature.likes.view.LikesScreen
import com.swahilib.feature.history.view.HistoryScreen
import com.swahilib.feature.auth.view.SignInScreen
import com.swahilib.feature.social.view.screen.SocialScreen
import com.swahilib.feature.how_it_works.view.HowItWorksScreen
import com.swahilib.feature.help.view.HelpScreen
import com.swahilib.feature.donation.view.screen.DonationScreen
import com.swahilib.feature.donation.view.screen.PaymentWebViewScreen
import com.swahilib.feature.daily_content.view.DailyWordScreen
import com.swahilib.feature.daily_content.view.DailyProverbScreen
import com.swahilib.feature.progress.view.screen.AchievementsScreen
import com.swahilib.feature.progress.view.screen.ChallengesScreen
import com.swahilib.feature.progress.view.screen.ProgressScreen
import com.swahilib.feature.progress.view.screen.StatisticsScreen
import com.swahilib.feature.progress.viewmodel.ProgressViewModel
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.feature.quiz.view.screen.QuizScreen
import com.swahilib.feature.quiz.viewmodel.QuizContentSource
import com.swahilib.feature.quiz.viewmodel.QuizViewModel
import com.swahilib.feature.word_builder.view.screen.WordBuilderScreen
import com.swahilib.feature.word_builder.viewmodel.WordBuilderViewModel
import com.swahilib.feature.sentence_builder.view.screen.SentenceBuilderScreen
import com.swahilib.feature.sentence_builder.viewmodel.SentenceBuilderViewModel
import com.swahilib.feature.spelling.view.screen.SpellingScreen
import com.swahilib.feature.spelling.viewmodel.SpellingViewModel
import com.swahilib.feature.crossword.view.screen.CrosswordScreen
import com.swahilib.feature.crossword.viewmodel.CrosswordViewModel
import com.swahilib.sudoku.view.SudokuScreen
import com.swahilib.sudoku.viewmodel.SudokuViewModel
import com.swahilib.feature.hangman.view.screen.HangmanScreen
import com.swahilib.feature.hangman.viewmodel.HangmanViewModel
import com.swahilib.core.games.model.SudokuTheme
import kotlinx.coroutines.launch

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
            val viewModel: com.swahilib.feature.settings.viewmodel.GameSettingsViewModel = hiltViewModel()
            com.swahilib.feature.settings.view.screen.GameSettingsScreen(navController = navController, viewModel = viewModel)
        }

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

        composable(Routes.ADVANCED_SEARCH) {
            val viewModel: AdvSearchViewModel = hiltViewModel()
            AdvSearchScreen(
                navController = navController,
                viewModel = viewModel,
                prefsRepo = prefsRepo
            )
        }

        composable(Routes.HOW_IT_WORKS) { HowItWorksScreen(navController = navController) }
        composable(Routes.HELP) { HelpScreen(navController = navController) }

        composable(Routes.DONATION) {
            val viewModel: DonationViewModel = hiltViewModel()
            DonationScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Routes.PAYMENT_WEBVIEW,
            arguments = listOf(
                navArgument("redirectUrl") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("redirectUrl") ?: ""
            val redirectUrl = Routes.decodeRedirectUrl(encoded)

            val donationEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.DONATION)
            }
            val viewModel: DonationViewModel = hiltViewModel(donationEntry)
            val scope = rememberCoroutineScope()

            PaymentWebViewScreen(
                navController = navController,
                viewModel = viewModel,
                redirectUrl = redirectUrl,
                onPaymentComplete = { isSuccess ->
                    if (isSuccess) {
                        scope.launch { prefsRepo.recordDonation() }
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    } else {
                        viewModel.resetState()
                        navController.popBackStack()
                    }
                },
            )
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

        composable(
            route = Routes.QUIZ,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = "BEGINNER" },
                navArgument("source") { type = NavType.StringType; defaultValue = "WORDS" },
            ),
        ) { backStackEntry ->
            val viewModel: QuizViewModel = hiltViewModel()
            val difficulty = runCatching {
                Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "BEGINNER")
            }.getOrDefault(Difficulty.BEGINNER)
            val source = runCatching {
                QuizContentSource.valueOf(backStackEntry.arguments?.getString("source") ?: "WORDS")
            }.getOrDefault(QuizContentSource.WORDS)
            QuizScreen(
                navController = navController,
                viewModel = viewModel,
                challengeId = backStackEntry.arguments?.getString("challengeId"),
                activityId = backStackEntry.arguments?.getString("activityId"),
                difficulty = difficulty,
                source = source,
            )
        }

        composable(
            route = Routes.WORD_BUILDER,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = "BEGINNER" },
                navArgument("timed") { type = NavType.BoolType; defaultValue = false },
                navArgument("endless") { type = NavType.BoolType; defaultValue = false },
            ),
        ) { backStackEntry ->
            val viewModel: WordBuilderViewModel = hiltViewModel()
            val difficulty = runCatching {
                Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "BEGINNER")
            }.getOrDefault(Difficulty.BEGINNER)
            WordBuilderScreen(
                navController = navController,
                viewModel = viewModel,
                challengeId = backStackEntry.arguments?.getString("challengeId"),
                activityId = backStackEntry.arguments?.getString("activityId"),
                difficulty = difficulty,
                timedMode = backStackEntry.arguments?.getBoolean("timed") ?: false,
                endless = backStackEntry.arguments?.getBoolean("endless") ?: false,
            )
        }

        composable(
            route = Routes.SENTENCE_BUILDER,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = "BEGINNER" },
            ),
        ) { backStackEntry ->
            val viewModel: SentenceBuilderViewModel = hiltViewModel()
            val difficulty = runCatching {
                Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "BEGINNER")
            }.getOrDefault(Difficulty.BEGINNER)
            SentenceBuilderScreen(
                navController = navController,
                viewModel = viewModel,
                challengeId = backStackEntry.arguments?.getString("challengeId"),
                activityId = backStackEntry.arguments?.getString("activityId"),
                difficulty = difficulty,
            )
        }

        composable(
            route = Routes.SPELLING,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = "BEGINNER" },
            ),
        ) { backStackEntry ->
            val viewModel: SpellingViewModel = hiltViewModel()
            val difficulty = runCatching {
                Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "BEGINNER")
            }.getOrDefault(Difficulty.BEGINNER)
            SpellingScreen(
                navController = navController,
                viewModel = viewModel,
                challengeId = backStackEntry.arguments?.getString("challengeId"),
                activityId = backStackEntry.arguments?.getString("activityId"),
                difficulty = difficulty,
            )
        }

        composable(
            route = Routes.CROSSWORD,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = "BEGINNER" },
            ),
        ) { backStackEntry ->
            val viewModel: CrosswordViewModel = hiltViewModel()
            val difficulty = runCatching {
                Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "BEGINNER")
            }.getOrDefault(Difficulty.BEGINNER)
            CrosswordScreen(
                navController = navController,
                viewModel = viewModel,
                challengeId = backStackEntry.arguments?.getString("challengeId"),
                activityId = backStackEntry.arguments?.getString("activityId"),
                difficulty = difficulty,
            )
        }

        composable(
            route = Routes.SUDOKU,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = "BEGINNER" },
                navArgument("theme") { type = NavType.StringType; defaultValue = "RANDOM" },
            ),
        ) { backStackEntry ->
            val viewModel: SudokuViewModel = hiltViewModel()
            val difficulty = runCatching {
                Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "BEGINNER")
            }.getOrDefault(Difficulty.BEGINNER)
            val theme = runCatching {
                SudokuTheme.valueOf(backStackEntry.arguments?.getString("theme") ?: "RANDOM")
            }.getOrDefault(SudokuTheme.RANDOM)
            SudokuScreen(
                navController = navController,
                viewModel = viewModel,
                challengeId = backStackEntry.arguments?.getString("challengeId"),
                activityId = backStackEntry.arguments?.getString("activityId"),
                difficulty = difficulty,
                theme = theme,
            )
        }

        composable(
            route = Routes.HANGMAN,
            arguments = listOf(
                navArgument("challengeId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("difficulty") { type = NavType.StringType; defaultValue = "BEGINNER" },
            ),
        ) { backStackEntry ->
            val viewModel: HangmanViewModel = hiltViewModel()
            val difficulty = runCatching {
                Difficulty.valueOf(backStackEntry.arguments?.getString("difficulty") ?: "BEGINNER")
            }.getOrDefault(Difficulty.BEGINNER)
            HangmanScreen(
                navController = navController,
                viewModel = viewModel,
                challengeId = backStackEntry.arguments?.getString("challengeId"),
                activityId = backStackEntry.arguments?.getString("activityId"),
                difficulty = difficulty,
            )
        }
    }
}
