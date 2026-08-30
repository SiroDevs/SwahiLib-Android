package com.swahilib.feature.word_builder.view.screen

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
import com.swahilib.core.ui.components.game.GameBottomBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameOverviewScreen
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameSoundFab
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelSelectContent
import com.swahilib.feature.word_builder.utils.WordBuilderUiState
import com.swahilib.feature.word_builder.view.components.PlayingContent
import com.swahilib.feature.word_builder.view.components.FinishedContent
import com.swahilib.feature.word_builder.viewmodel.WordBuilderViewModel

private val WORD_BUILDER_INSTRUCTIONS = listOf(
    "Gusa vipande vya herufi kwa mpangilio sahihi kuunda neno la Kiswahili.",
    "Tumia 'Kidokezo' ukikwama - lakini kila kidokezo hupunguza sign za mzunguko huo.",
    "Kila kiwango depth muda maalum kwa kila neno; ukiisha muda, mchezo utaendelea kiotomatiki.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBuilderScreen(
    navController: NavHostController,
    viewModel: WordBuilderViewModel,
    challengeId: String?,
    activityId: String?,
    difficulty: Difficulty = Difficulty.BEGINNER,
    timedMode: Boolean = false,
    endless: Boolean = false,
) {
    LaunchedEffect(challengeId, activityId) {
        viewModel.start(
            challengeId = challengeId,
            activityId = activityId,
            difficulty = difficulty,
            timedMode = timedMode,
            endless = endless,
        )
    }
    val state by viewModel.uiState.collectAsState()
    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    val isPlaying = state is WordBuilderUiState.Playing
    BackHandler(enabled = isPlaying) { showExit = true }

    if (showRestart) {
        GameRestartDialog(
            onConfirm = { showRestart = false; viewModel.restart() },
            onDismiss = { showRestart = false })
    }
    if (showExit) {
        GameExitDialog(
            onGoBackDiscard = {
                showExit = false; viewModel.discardAndExit { navController.popBackStack() }
            },
            onSaveAndGoBack = {
                showExit = false; viewModel.saveAndExit { navController.popBackStack() }
            },
            onCancel = { showExit = false },
        )
    }

    Scaffold(
        topBar = {
            when (val s = state) {
                is WordBuilderUiState.Playing -> GameTopBar(
                    title = "Jenga Maneno",
                    level = s.level,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )

                else -> AppTopBar(
                    title = "Jenga Maneno",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
        bottomBar = {
            val s = state
            if (s is WordBuilderUiState.Playing) {
                GameBottomBar(
                    remainingSeconds = s.secondsRemaining,
                    totalSeconds = s.secondsTotal,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    paused = s.paused,
                    onTogglePause = viewModel::togglePause,
                    onAction = { viewModel.submit(); viewModel.continueToNext() },
                    actionEnabled = !s.locked && !s.paused && s.assembled.length == s.word.answer.length,
                )
            }
        },
        floatingActionButton = {
            if (isPlaying) GameSoundFab(viewModel.soundPlayer)
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                is WordBuilderUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is WordBuilderUiState.Empty -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Hamna maneno ya kutosha kwa sasa.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is WordBuilderUiState.Overview -> GameOverviewScreen(
                    title = "Jenga Maneno",
                    tagline = "Panga vipande vya herufi kuunda neno sahihi.",
                    instructions = WORD_BUILDER_INSTRUCTIONS,
                    onStart = viewModel::proceedToLevelSelect,
                    onPractice = viewModel::startPractice,
                )

                is WordBuilderUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = { model -> viewModel.chooseLevel(model.level) },
                )

                is WordBuilderUiState.Playing -> AnimatedContent(
                    targetState = s,
                    contentKey = { it.roundIndex },
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "wordBuilderRound",
                ) { playingState ->
                    PlayingContent(
                        state = playingState,
                        onPick = viewModel::pickLetter,
                        onClear = viewModel::clearPicks,
                        onHint = viewModel::useHint,
                        onTogglePause = viewModel::togglePause,
                    )
                }

                is WordBuilderUiState.Finished -> FinishedContent(
                    state = s,
                    soundPlayer = viewModel.soundPlayer,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}
