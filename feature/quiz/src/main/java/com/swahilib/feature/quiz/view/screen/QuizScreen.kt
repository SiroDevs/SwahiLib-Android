package com.swahilib.feature.quiz.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.feature.quiz.utils.QuizUiState
import com.swahilib.feature.quiz.view.components.PlayingState
import com.swahilib.feature.quiz.view.components.ResultState
import com.swahilib.feature.quiz.viewmodel.QuizContentSource
import com.swahilib.feature.quiz.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavHostController,
    viewModel: QuizViewModel,
    challengeId: String?,
    activityId: String?,
    difficulty: Difficulty = Difficulty.BEGINNER,
    source: QuizContentSource = QuizContentSource.WORDS,
) {
    LaunchedEffect(challengeId, activityId) {
        viewModel.start(challengeId = challengeId, activityId = activityId, difficulty = difficulty, source = source)
    }
    val state by viewModel.uiState.collectAsState()
    val title = if (source == QuizContentSource.PROVERBS) "Changamoto ya Methali" else "Jaribio la Msamiati"

    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    val isPlaying = state is QuizUiState.Playing
    BackHandler(enabled = isPlaying) { showExit = true }

    if (showRestart) {
        GameRestartDialog(onConfirm = { showRestart = false; viewModel.restart() }, onDismiss = { showRestart = false })
    }
    if (showExit) {
        GameExitDialog(
            onGoBackDiscard = { showExit = false; viewModel.discardAndExit { navController.popBackStack() } },
            onSaveAndGoBack = { showExit = false; viewModel.saveAndExit { navController.popBackStack() } },
            onCancel = { showExit = false },
        )
    }

    Scaffold(
        topBar = {
            when (val s = state) {
                is QuizUiState.Playing -> GameTopBar(
                    title = title,
                    level = null,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )
                else -> AppTopBar(title = title, showGoBack = true, onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is QuizUiState.Loading -> LoadingState()
                is QuizUiState.Empty -> EmptyState()
                is QuizUiState.Playing -> AnimatedContent(
                    targetState = s.index,
                    transitionSpec = { (fadeIn(tween(220))) togetherWith (fadeOut(tween(160))) },
                    label = "quizQuestion",
                ) {
                    PlayingState(
                        state = s,
                        onChoice = viewModel::submitChoice,
                        onTyped = viewModel::submitTyped,
                        onMatches = viewModel::submitMatches,
                    )
                }
                is QuizUiState.Finished -> ResultState(
                    result = s.result,
                    quizSet = s.quizSet,
                    answers = s.answers,
                    unlockedAchievements = s.unlockedAchievements,
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            "Hakuna maneno ya kutosha kwenye kamusi kuunda jaribio kwa sasa.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

