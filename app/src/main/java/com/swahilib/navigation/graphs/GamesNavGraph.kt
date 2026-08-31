package com.swahilib.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.SudokuTheme
import com.swahilib.feature.crossword.view.screen.CrosswordScreen
import com.swahilib.feature.crossword.viewmodel.CrosswordViewModel
import com.swahilib.feature.hangman.view.screen.HangmanScreen
import com.swahilib.feature.hangman.viewmodel.HangmanViewModel
import com.swahilib.feature.quiz.view.screen.QuizScreen
import com.swahilib.feature.quiz.viewmodel.QuizContentSource
import com.swahilib.feature.quiz.viewmodel.QuizViewModel
import com.swahilib.feature.sentence_builder.view.screen.SentenceBuilderScreen
import com.swahilib.feature.sentence_builder.viewmodel.SentenceBuilderViewModel
import com.swahilib.feature.spelling.view.screen.SpellingScreen
import com.swahilib.feature.spelling.viewmodel.SpellingViewModel
import com.swahilib.feature.word_builder.view.screen.WordBuilderScreen
import com.swahilib.feature.word_builder.viewmodel.WordBuilderViewModel
import com.swahilib.feature.sudoku.view.screen.SudokuScreen
import com.swahilib.sudoku.viewmodel.SudokuViewModel

fun NavGraphBuilder.gamesGraph(navController: NavHostController) {
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
