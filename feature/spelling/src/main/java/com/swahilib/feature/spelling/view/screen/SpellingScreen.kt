package com.swahilib.feature.spelling.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.swahilib.feature.spelling.utils.SpellingUiState
import com.swahilib.feature.spelling.view.components.PlayingSpelling
import com.swahilib.feature.spelling.viewmodel.SpellingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellingScreen(
    navController: NavHostController,
    viewModel: SpellingViewModel,
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
    val isPlaying = state is SpellingUiState.Playing
    BackHandler(enabled = isPlaying) { showExit = true }

    val questionId = (state as? SpellingUiState.Playing)?.question?.id
    var typed by remember(questionId) { mutableStateOf("") }

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
                is SpellingUiState.Playing -> GameTopBar(
                    title = "Tahajia",
                    level = s.level,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                    isPractice = s.practice,
                )

                else -> AppTopBar(
                    title = "Tahajia",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
        bottomBar = {
            val s = state
            if (s is SpellingUiState.Playing) {
                val inputEnabled = !s.locked && !s.paused
                GameBottomBar(
                    remainingSeconds = s.secondsRemaining,
                    totalSeconds = s.secondsTotal,
                    paused = s.paused,
                    onTogglePause = viewModel::togglePause,
                    onAction = { viewModel.submit(typed); viewModel.continueToNext() },
                    actionEnabled = inputEnabled && typed.isNotBlank(),
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
                is SpellingUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is SpellingUiState.Empty -> Box(
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

                is SpellingUiState.Overview -> GameOverviewScreen(
                    tagline = "Andika tahajia sahihi ya maneno ya Kiswahili.",
                    instructions = Instructions.SPELLING,
                    onStart = viewModel::proceedToLevelSelect,
                    onPractice = viewModel::startPractice,
                )

                is SpellingUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = { model -> viewModel.chooseLevel(model.level) },
                )

                is SpellingUiState.Playing -> AnimatedContent(
                    targetState = s,
                    contentKey = { it.index },
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "spellingRound",
                ) { playingState ->
                    PlayingSpelling(
                        state = playingState,
                        typed = typed,
                        onTypedChange = { typed = it },
                        onHint = viewModel::useHint,
                        onTogglePause = viewModel::togglePause,
                    )
                }

                is SpellingUiState.Finished -> GameFinished(
                    practice = s.practice,
                    headline = if (s.result.isPerfect) "\ud83c\udf89 Tahajia Kamili!" else "Umemaliza!",
                    statLines = listOf(
                        "${s.result.fullyCorrectCount}/${s.result.totalQuestions} sahihi kabisa",
                        "Wastani wa usahihi: ${(s.result.averageCredit * 100).toInt()}%",
                    ),
                    xpEarned = s.result.xpEarned,
                    level = s.level,
                    pointsEarned = s.pointsEarned,
                    unlockedAchievements = s.unlockedAchievements,
                    soundPlayer = viewModel.soundPlayer,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                    reviewItems = {
                        items(s.questions.size) { i ->
                            val question = s.questions[i]
                            val result = s.rounds.getOrNull(i)
                            val correct = result?.fullyCorrect == true
                            GameReviewRow(
                                correct = correct,
                                primaryText = question.answer,
                                secondaryText = question.clue,
                                tertiaryText = if (!correct && result != null) "Umeandika: \"${result.typed}\"" else null,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    },
                )
            }
        }
    }
}
