package com.swahilib.feature.spelling.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.swahilib.core.games.model.SpellingQuestion
import com.swahilib.core.games.model.SpellingRoundResult
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.core.ui.components.game.StepTimerBar
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.spelling.viewmodel.SpellingUiState
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
                is SpellingUiState.Playing -> GameTopBar(
                    title = "Tahajia (Spellcheck)",
                    level = s.level,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )
                else -> AppTopBar(title = "Tahajia (Spellcheck)", showGoBack = true, onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is SpellingUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is SpellingUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna maneno ya kutosha kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is SpellingUiState.LevelSelect -> LevelSelectContent(s.previousPoints, s.levels, viewModel::chooseLevel)
                is SpellingUiState.Playing -> AnimatedContent(
                    targetState = s.index,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "spellingRound",
                ) {
                    PlayingContent(state = s, onHint = viewModel::useHint, onSubmit = viewModel::submit)
                }
                is SpellingUiState.Finished -> FinishedContent(
                    state = s,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun LevelSelectContent(previousPoints: Int, levels: List<GameLevelUiModel>, onLevelTap: (GameLevelUiModel) -> Unit) {
    Column(Modifier.fillMaxSize().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Chagua Kiwango", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(4.dp))
        Text("Jumla ya alama: $previousPoints", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        LevelCarousel(levels = levels, onLevelTap = onLevelTap)
    }
}

@Composable
private fun PlayingContent(state: SpellingUiState.Playing, onHint: () -> Unit, onSubmit: (String) -> Unit) {
    var typed by remember(state.question.id) { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        StepTimerBar(remainingSeconds = state.secondsRemaining, totalSeconds = state.secondsTotal)
        Spacer(Modifier.height(16.dp))
        Text(
            "Neno ${state.index + 1}/${state.questions.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Andika neno la Kiswahili lenye maana:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(state.question.clue, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(16.dp))

        if (state.revealedLetters > 0) {
            Text(
                text = "${state.hintText[0].uppercaseChar()}${state.hintText.drop(1)} ni?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = typed,
            onValueChange = { if (!state.locked) typed = it },
            label = { Text("Jibu lako") },
            singleLine = true,
            enabled = !state.locked,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onHint, enabled = !state.locked, modifier = Modifier.weight(1f)) { Text("Kidokezo") }
            Button(onClick = { onSubmit(typed) }, enabled = !state.locked && typed.isNotBlank(), modifier = Modifier.weight(1f)) {
                Text("Wasilisha")
            }
        }
    }
}

@Composable
private fun FinishedContent(state: SpellingUiState.Finished, onPlayAgain: () -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (state.result.isPerfect) "\ud83c\udf89 Tahajia Kamili!" else "Umemaliza!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(12.dp))
        Text("${state.result.fullyCorrectCount}/${state.result.totalQuestions} sahihi kabisa", style = MaterialTheme.typography.titleMedium)
        Text("Wastani wa usahihi: ${(state.result.averageCredit * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
        Text("+${state.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        if (state.level != null) {
            Text("+${state.pointsEarned} alama - Kiwango ${state.level}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(16.dp))
        AchievementUnlockBanner(state.unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

        Text("Majibu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.questions.size) { i ->
                SpellingReviewRow(state.questions[i], state.rounds.getOrNull(i))
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.level != null) {
                Button(onClick = onPlayAgain, modifier = Modifier.weight(1f)) { Text("Viwango") }
            }
            Button(onClick = onDone, modifier = Modifier.weight(1f)) { Text("Sawa") }
        }
    }
}

@Composable
private fun SpellingReviewRow(question: SpellingQuestion, result: SpellingRoundResult?) {
    val correct = result?.fullyCorrect == true
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (correct) Icons.Default.CheckCircle else Icons.Default.Close, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(question.answer, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                Text(question.clue, style = MaterialTheme.typography.bodySmall)
                if (!correct && result != null) {
                    Text("Umeandika: \"${result.typed}\"", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
