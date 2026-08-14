package com.swahilib.feature.quiz.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.swahilib.core.ui.components.game.CelebrationOverlay
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
                    soundPlayer = viewModel.soundPlayer,
                )

                else -> AppTopBar(
                    title = title,
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
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

            CelebrationOverlay(
                visible = showCelebration,
                onDismiss = { showCelebration = false },
                soundPlayer = viewModel.soundPlayer,
            )
        }
    }
}

@Composable
private fun SetupState(
    state: QuizUiState.Setup,
    onDifficulty: (Difficulty) -> Unit,
    onCountDelta: (Int) -> Unit,
    onStart: () -> Unit,
    onPractice: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Weka Mpangilio wa Jaribio", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(4.dp))
        Text(
            "Jumla ya alama ulizowahi kupata: ${state.previousPoints}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))

        Text("Kiwango cha Ugumu", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Difficulty.entries.forEach { d ->
                FilterChip(
                    selected = state.difficulty == d,
                    onClick = { onDifficulty(d) },
                    label = { Text(d.swahiliLabel()) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer),
                )
            }
        }
        Spacer(Modifier.height(28.dp))

        Text("Idadi ya Maswali", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh), modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onCountDelta(-1) }, enabled = state.questionCount > 3) {
                    Icon(Icons.Default.Remove, contentDescription = "Punguza")
                }
                Text("${state.questionCount}", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                IconButton(onClick = { onCountDelta(1) }, enabled = state.questionCount < 50) {
                    Icon(Icons.Default.Add, contentDescription = "Ongeza")
                }
            }
        }
        Text(
            "Chagua kati ya maswali 3 hadi 50.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )

        Spacer(Modifier.weight(1f))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Anza Jaribio") }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onPractice, modifier = Modifier.fillMaxWidth()) { Text("Jaribu Kwanza (Mazoezi ya Maswali 3)") }
    }
}

private fun Difficulty.swahiliLabel(): String = when (this) {
    Difficulty.BEGINNER -> "Rahisi"
    Difficulty.INTERMEDIATE -> "Wastani"
    Difficulty.ADVANCED -> "Ngumu"
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier
        .fillMaxSize()
        .padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            "Hakuna maneno ya kutosha kwenye kamusi kuunda jaribio kwa sasa.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

