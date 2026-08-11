package com.swahilib.feature.word_builder.view

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.ScrambledWord
import com.swahilib.core.games.model.WordBuilderRoundResult
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.core.ui.components.game.StepTimerBar
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.word_builder.viewmodel.WordBuilderUiState
import com.swahilib.feature.word_builder.viewmodel.WordBuilderViewModel

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
                is WordBuilderUiState.Playing -> GameTopBar(
                    title = "Jenga Maneno",
                    level = s.level,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )
                else -> AppTopBar(title = "Jenga Maneno", showGoBack = true, onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is WordBuilderUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is WordBuilderUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hamna maneno ya kutosha kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is WordBuilderUiState.LevelSelect -> LevelSelectContent(s.previousPoints, s.levels, viewModel::chooseLevel)
                is WordBuilderUiState.Playing -> AnimatedContent(
                    targetState = s.roundIndex,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "wordBuilderRound",
                ) {
                    PlayingContent(
                        state = s,
                        onPick = viewModel::pickLetter,
                        onClear = viewModel::clearPicks,
                        onHint = viewModel::useHint,
                        onSubmit = viewModel::submit,
                    )
                }
                is WordBuilderUiState.Finished -> FinishedContent(
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
private fun PlayingContent(
    state: WordBuilderUiState.Playing,
    onPick: (Int) -> Unit,
    onClear: () -> Unit,
    onHint: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        StepTimerBar(remainingSeconds = state.secondsRemaining, totalSeconds = state.secondsTotal)
        Spacer(Modifier.height(16.dp))
        Text(
            "Neno ${state.roundIndex + 1}/${state.totalRounds}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        if (state.word.hint.isNotBlank()) {
            Text(
                "Kidokezo: ${state.word.hint}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                state.assembled.ifBlank { " " },
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
            state.word.scrambledLetters.forEachIndexed { index, letter ->
                val used = index in state.pickedIndices
                LetterTile(letter = letter, used = used, enabled = !state.locked && !used, onClick = { onPick(index) })
            }
        }
        Spacer(Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onClear, enabled = !state.locked, modifier = Modifier.weight(1f)) { Text("Futa") }
            OutlinedButton(onClick = onHint, enabled = !state.locked, modifier = Modifier.weight(1f)) { Text("Kidokezo") }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onSubmit,
            enabled = !state.locked && state.assembled.length == state.word.answer.length,
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Tuma") }
    }
}

@Composable
private fun LetterTile(letter: Char, used: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = if (used) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.size(44.dp),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                letter.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (used) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun FinishedContent(state: WordBuilderUiState.Finished, onPlayAgain: () -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (state.result.isPerfect) "\ud83c\udf89 Kamili Bila Kidokezo!" else "Umemaliza!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(12.dp))
        Text("${state.result.correctWords}/${state.result.totalWords} maneno sahihi", style = MaterialTheme.typography.titleMedium)
        Text("+${state.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        if (state.level != null) {
            Text("+${state.pointsEarned} alama - Kiwango ${state.level}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(16.dp))
        AchievementUnlockBanner(state.unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

        Text("Majibu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.rounds) { (word, result) -> WordReviewRow(word, result) }
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
private fun WordReviewRow(word: ScrambledWord, result: WordBuilderRoundResult) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (result.correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (result.correct) Icons.Default.CheckCircle else Icons.Default.Close, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(word.answer.uppercase(), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                if (word.hint.isNotBlank()) Text(word.hint, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
