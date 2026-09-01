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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.QuizFormat
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.CelebrationOverlay
import com.swahilib.core.ui.components.game.GameBottomBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameSoundFab
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.feature.quiz.utils.QuizUiState
import com.swahilib.feature.quiz.view.components.EmptyState
import com.swahilib.feature.quiz.view.components.LoadingState
import com.swahilib.feature.quiz.view.components.PlayingState
import com.swahilib.feature.quiz.view.components.ResultState
import com.swahilib.feature.quiz.view.components.SetupState
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
        viewModel.start(
            challengeId = challengeId,
            activityId = activityId,
            difficulty = difficulty,
            source = source
        )
    }
    val state by viewModel.uiState.collectAsState()
    val title =
        if (source == QuizContentSource.PROVERBS) "Changamoto ya Methali" else "Jaribio la Msamiati"

    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    val isPlaying = state is QuizUiState.Playing
    BackHandler(enabled = isPlaying) { showExit = true }

    val questionId = (state as? QuizUiState.Playing)?.question?.id
    var selectedOptionId by remember(questionId) { mutableStateOf<String?>(null) }
    var typedText by remember(questionId) { mutableStateOf("") }
    var matchedPairs by remember(questionId) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(state) {
        if (state is QuizUiState.Finished) showCelebration = true
    }

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
                is QuizUiState.Playing -> GameTopBar(
                    title = title,
                    level = null,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )

                else -> AppTopBar(
                    title = title,
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
        bottomBar = {
            val s = state
            if (s is QuizUiState.Playing) {
                val inputEnabled = !s.answered && !s.paused
                val hasDraftAnswer = when (s.question.format) {
                    QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> selectedOptionId != null
                    QuizFormat.FILL_IN_BLANK -> typedText.isNotBlank()
                    QuizFormat.MATCH_WORDS -> matchedPairs.size == s.question.matchLeft.size
                }
                GameBottomBar(
                    remainingSeconds = s.secondsRemaining,
                    totalSeconds = s.secondsTotal,
                    paused = s.paused,
                    onTogglePause = viewModel::togglePause,
                    onAction = {
                        when (s.question.format) {
                            QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> selectedOptionId?.let(viewModel::submitChoice)
                            QuizFormat.FILL_IN_BLANK -> viewModel.submitTyped(typedText)
                            QuizFormat.MATCH_WORDS -> viewModel.submitMatches(matchedPairs)
                        }
                        viewModel.continueToNext()
                    },
                    actionEnabled = hasDraftAnswer && inputEnabled,
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
                is QuizUiState.Loading -> LoadingState()
                is QuizUiState.Empty -> EmptyState()
                is QuizUiState.Setup -> SetupState(
                    state = s,
                    onDifficulty = viewModel::updateSetupDifficulty,
                    onCountDelta = viewModel::updateSetupCount,
                    onStart = { viewModel.confirmSetup(practice = false) },
                    onPractice = { viewModel.confirmSetup(practice = true) },
                )
                is QuizUiState.Playing -> AnimatedContent(
                    targetState = s,
                    contentKey = { it.index },
                    transitionSpec = { (fadeIn(tween(220))) togetherWith (fadeOut(tween(160))) },
                    label = "quizQuestion",
                ) { playingState ->
                    PlayingState(
                        state = playingState,
                        selectedOptionId = selectedOptionId,
                        onSelectOption = { selectedOptionId = it },
                        typedText = typedText,
                        onTypedTextChange = { typedText = it },
                        matchedPairs = matchedPairs,
                        onMatchedPairsChange = { matchedPairs = it },
                        onTogglePause = viewModel::togglePause,
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

            CelebrationOverlay(
                visible = showCelebration,
                onDismiss = { showCelebration = false },
                soundPlayer = viewModel.soundPlayer,
            )
        }
    }
}
