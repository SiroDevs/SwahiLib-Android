package com.swahilib.feature.sudoku.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import com.swahilib.core.games.model.SudokuTheme
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameFinished
import com.swahilib.core.ui.components.game.GameOverviewScreen
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameReviewRow
import com.swahilib.core.ui.components.game.GameSoundFab
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelSelectContent
import com.swahilib.feature.sudoku.utils.SudokuUiState
import com.swahilib.feature.sudoku.view.components.PlayingSudoku
import com.swahilib.feature.sudoku.view.components.SudokuLetterPoolBar
import com.swahilib.sudoku.viewmodel.SudokuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SudokuScreen(
    navController: NavHostController,
    viewModel: SudokuViewModel,
    challengeId: String?,
    activityId: String?,
    difficulty: Difficulty = Difficulty.BEGINNER,
    theme: SudokuTheme = SudokuTheme.RANDOM,
) {
    LaunchedEffect(challengeId, activityId) {
        viewModel.start(
            challengeId = challengeId,
            activityId = activityId,
            difficulty = difficulty,
            theme = theme
        )
    }
    val state by viewModel.uiState.collectAsState()
    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    val isPlaying = state is SudokuUiState.Playing
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
                is SudokuUiState.Playing -> GameTopBar(
                    title = "Sudoku",
                    level = s.level,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )

                else -> AppTopBar(
                    title = "Sudoku",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
        floatingActionButton = {
            if (isPlaying) GameSoundFab(viewModel.soundPlayer)
        },
        bottomBar = {
            val s = state
            if (s is SudokuUiState.Playing && s.easyMode) {
                SudokuLetterPoolBar(
                    letters = s.letterPool,
                    highlighted = s.highlightedLetter,
                    onLetter = viewModel::tapPoolLetter
                )
            }
        },
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val s = state) {
                is SudokuUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is SudokuUiState.Empty -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Hakuna maneno ya kutosha kwenye sudoku kwa sasa.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is SudokuUiState.Overview -> GameOverviewScreen(
                    title = "Sudoku",
                    tagline = "Tafuta maneno ya Kiswahili yaliyofichwa kwenye gridi.",
                    instructions = Instructions.SUDOKU,
                    onStart = viewModel::proceedToLevelSelect,
                    onPractice = viewModel::startPractice,
                )

                is SudokuUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = { model -> viewModel.chooseLevel(model.level) },
                )

                is SudokuUiState.Playing -> PlayingSudoku(
                    state = s,
                    onTapCell = viewModel::tapCell,
                    onGiveUp = viewModel::giveUp,
                    onTogglePause = viewModel::togglePause,
                )

                is SudokuUiState.Finished -> GameFinished(
                    practice = s.practice,
                    headline = if (s.result.isPerfect) "\ud83c\udf89 Umeshinda Yote!" else "Umemaliza!",
                    statLines = listOf("${s.result.foundWords}/${s.result.totalWords} umeshinda"),
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
                        items(s.words, key = { it.word }) { word ->
                            GameReviewRow(
                                correct = word.found,
                                primaryText = word.word,
                                secondaryText = word.clue,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                )
            }
        }
    }
}
