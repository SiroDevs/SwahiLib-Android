package com.swahilib.feature.hangman.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.swahilib.core.games.model.HangmanRound
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.core.ui.components.game.StepTimerBar
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.hangman.viewmodel.HangmanUiState
import com.swahilib.feature.hangman.viewmodel.HangmanViewModel

private val ALPHABET = ('A'..'Z').toList()

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
                is HangmanUiState.Playing -> GameTopBar(
                    title = "Hangman",
                    level = s.level,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )
                else -> AppTopBar(title = "Hangman", showGoBack = true, onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is HangmanUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is HangmanUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna maneno ya kutosha kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is HangmanUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = viewModel::chooseLevel,
                )
                is HangmanUiState.Playing -> AnimatedContent(
                    targetState = s.index,
                    transitionSpec = {
                        (slideInHorizontally(tween(280)) { it } + fadeIn()) togetherWith
                            (slideOutHorizontally(tween(280)) { -it } + fadeOut())
                    },
                    label = "hangmanRound",
                ) {
                    PlayingContent(s, onGuess = viewModel::guess)
                }
                is HangmanUiState.Finished -> FinishedContent(
                    state = s,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun LevelSelectContent(previousPoints: Int, levels: List<com.swahilib.core.ui.components.game.GameLevelUiModel>, onLevelTap: (com.swahilib.core.ui.components.game.GameLevelUiModel) -> Unit) {
    Column(Modifier.fillMaxSize().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Chagua Kiwango", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(4.dp))
        Text("Jumla ya alama: $previousPoints", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        LevelCarousel(levels = levels, onLevelTap = { onLevelTap(it) })
    }
}

@Composable
private fun PlayingContent(state: HangmanUiState.Playing, onGuess: (Char) -> Unit) {
    val round = state.round
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        StepTimerBar(remainingSeconds = state.secondsRemaining, totalSeconds = state.secondsTotal)
        Spacer(Modifier.height(16.dp))
        Text(
            "Neno ${state.index + 1}/${state.rounds.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Makosa: ${round.wrongGuesses}/${round.maxWrongGuesses}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (round.wrongGuesses >= round.maxWrongGuesses - 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        if (round.hint.isNotBlank()) {
            Text("Kidokezo: ${round.hint}", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh), modifier = Modifier.fillMaxWidth()) {
            Text(
                round.displayWord,
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
        Spacer(Modifier.height(24.dp))

        LetterGrid(guessed = round.guessedLetters, answer = round.answer, enabled = !round.isOver, onGuess = onGuess)
    }
}

@Composable
private fun LetterGrid(guessed: Set<Char>, answer: String, enabled: Boolean, onGuess: (Char) -> Unit) {
    val rows = ALPHABET.chunked(7)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth()) {
                row.forEach { letter ->
                    val used = letter in guessed
                    val correct = used && letter in answer
                    Card(
                        onClick = { if (!used && enabled) onGuess(letter) },
                        enabled = !used && enabled,
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                !used -> MaterialTheme.colorScheme.primaryContainer
                                correct -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.errorContainer
                            },
                        ),
                        modifier = Modifier.size(32.dp),
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(letter.toString(), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinishedContent(state: HangmanUiState.Finished, onPlayAgain: () -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(8.dp))
        Text(
            if (state.result.isPerfect) "\ud83c\udf89 Umeshinda Yote!" else "Umemaliza!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(12.dp))
        Text("${state.result.wonWords}/${state.result.totalWords} umeshinda", style = MaterialTheme.typography.titleMedium)
        Text("+${state.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        if (state.level != null) {
            Text("+${state.pointsEarned} alama - Kiwango ${state.level}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        AnimatedVisibility(visible = state.leveledUp) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                Text("Kiwango kipya kimefunguliwa!", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(Modifier.height(16.dp))
        AchievementUnlockBanner(state.unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

        Text("Majibu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.rounds) { round -> RoundReviewRow(round) }
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.level != null) {
                Button(onClick = onPlayAgain, modifier = Modifier.weight(1f)) {
                    Text("Viwango")
                }
            }
            Button(onClick = onDone, modifier = Modifier.weight(1f)) {
                Text("Sawa")
            }
        }
    }
}

@Composable
private fun RoundReviewRow(round: HangmanRound) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (round.isWon) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (round.isWon) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(round.answer, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                if (round.hint.isNotBlank()) {
                    Text(round.hint, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
