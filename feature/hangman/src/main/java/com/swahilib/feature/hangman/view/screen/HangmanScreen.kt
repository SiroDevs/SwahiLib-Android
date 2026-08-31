package com.swahilib.feature.hangman.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.swahilib.core.common.utils.Instructions
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameBottomBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameFinished
import com.swahilib.core.ui.components.game.GameOverviewScreen
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameReviewRow
import com.swahilib.core.ui.components.game.GameSoundFab
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelSelectContent
import com.swahilib.feature.hangman.utils.HangmanUiState
import com.swahilib.feature.hangman.view.components.PlayingHangman
import com.swahilib.feature.hangman.viewmodel.HangmanViewModel

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
                    isPractice = s.practice,
                )

                else -> AppTopBar(
                    title = "Hangman",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
        bottomBar = {
            val s = state
            if (s is HangmanUiState.Playing) {
                GameBottomBar(
                    remainingSeconds = s.secondsRemaining,
                    totalSeconds = s.secondsTotal,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    paused = s.paused,
                    onTogglePause = viewModel::togglePause,
                    onAction = viewModel::continueToNext,
                    actionEnabled = s.round.isOver && !s.paused,
                )
            }
        },
        floatingActionButton = {
            if (isPlaying) GameSoundFab(viewModel.soundPlayer)
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
                    tagline = "Kisia neno la kiswahili herufi kwa herufi.",
                    instructions = Instructions.HANGMAN,
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
                    PlayingHangman(
                        state = playingState,
                        onGuess = viewModel::guess,
                        onTogglePause = viewModel::togglePause,
                    )
                }

                is HangmanUiState.Finished -> GameFinished(
                    practice = s.practice,
                    headline = if (s.result.isPerfect) "\ud83c\udf89 Umeshinda Yote!" else "Umemaliza!",
                    statLines = listOf("${s.result.wonWords}/${s.result.totalWords} umeshinda"),
                    xpEarned = s.result.xpEarned,
                    level = s.level,
                    pointsEarned = s.pointsEarned,
                    unlockedAchievements = s.unlockedAchievements,
                    soundPlayer = viewModel.soundPlayer,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                    extraContent = {
                        AnimatedVisibility(visible = s.leveledUp) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                                modifier = Modifier.padding(top = 12.dp),
                            ) {
                                Text("Kiwango kipya kimefunguliwa!", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    },
                    reviewItems = {
                        items(s.rounds) { round ->
                            GameReviewRow(
                                correct = round.isWon,
                                primaryText = round.answer,
                                secondaryText = round.hint.takeIf { it.isNotBlank() },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                )
            }
        }
    }
}
