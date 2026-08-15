package com.swahilib.feature.hangman.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameOverviewScreen
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.feature.hangman.utils.HangmanUiState
import com.swahilib.feature.hangman.view.components.PlayingContent
import com.swahilib.feature.hangman.view.components.FinishedContent
import com.swahilib.feature.hangman.viewmodel.HangmanViewModel

private val HANGMAN_INSTRUCTIONS = listOf(
    "Bofya herufi kukisia neno la siri kabla ya kukosea mara sita.",
    "Kila kiwango depth muda maalum kwa kila neno - ukiisha muda, mchezo utaendelea kiotomatiki.",
    "Majibu hayaonyeshwi mpaka mwisho wa mchezo, kisha utaona ukaguzi kamili.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HangmanScreen(
    navController: NavHostController,
    viewModel: HangmanViewModel,
    challengeId: String?,
    activityId: String?,
    difficulty: Difficulty = Difficulty.BEGINNER,
) {
    LaunchedEffect(challengeId, activityId) {
        viewModel.start(challengeId = challengeId, activityId = activityId, difficulty = difficulty)
    }
    val state by viewModel.uiState.collectAsState()
    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }

    val isPlaying = state is HangmanUiState.Playing
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
                is HangmanUiState.Playing -> GameTopBar(
                    title = "Hangman",
                    level = s.level,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                    soundPlayer = viewModel.soundPlayer,
                )

                else -> AppTopBar(
                    title = "Hangman",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val s = state) {
                is HangmanUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is HangmanUiState.Empty -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Hakuna maneno ya kutosha kwa sasa.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is HangmanUiState.Overview -> GameOverviewScreen(
                    title = "Hangman",
                    tagline = "Kisia neno la Kiswahili herufi kwa herufi.",
                    instructions = HANGMAN_INSTRUCTIONS,
                    onStart = viewModel::proceedToLevelSelect,
                    onPractice = viewModel::startPractice,
                )

                is HangmanUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = { model -> viewModel.chooseLevel(model.level) },
                )

                is HangmanUiState.Playing -> AnimatedContent(
                    targetState = s,
                    contentKey = { it.index },
                    transitionSpec = {
                        (slideInHorizontally(tween(280)) { it } + fadeIn()) togetherWith
                            (slideOutHorizontally(tween(280)) { -it } + fadeOut())
                    },
                    label = "hangmanRound",
                ) { playingState ->
                    PlayingContent(
                        state = playingState,
                        onGuess = viewModel::guess,
                        onTogglePause = viewModel::togglePause,
                        onContinue = viewModel::continueToNext,
                    )
                }

                is HangmanUiState.Finished -> FinishedContent(
                    state = s,
                    soundPlayer = viewModel.soundPlayer,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun LevelSelectContent(
    previousPoints: Int,
    levels: List<GameLevelUiModel>,
    onLevelTap: (GameLevelUiModel) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Chagua Kiwango",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Jumla ya sign: $previousPoints",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        LevelCarousel(levels = levels, onLevelTap = { onLevelTap(it) })
    }
}
