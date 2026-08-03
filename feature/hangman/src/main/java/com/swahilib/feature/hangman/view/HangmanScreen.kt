package com.swahilib.feature.hangman.view

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
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.AchievementUnlockBanner
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

    Scaffold(
        topBar = {
            AppTopBar(title = "Hangman", showGoBack = true, onNavIconClick = { navController.popBackStack() })
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
                is HangmanUiState.Playing -> PlayingContent(s, onGuess = viewModel::guess, onNext = viewModel::next)
                is HangmanUiState.Finished -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        if (s.result.isPerfect) "🎉 Umeshinda Yote!" else "Umemaliza!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("${s.result.wonWords}/${s.result.totalWords} umeshinda", style = MaterialTheme.typography.titleMedium)
                    Text("+${s.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(24.dp))
                    AchievementUnlockBanner(s.unlockedAchievements, modifier = Modifier.padding(bottom = 16.dp))
                    Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Sawa")
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayingContent(state: HangmanUiState.Playing, onGuess: (Char) -> Unit, onNext: () -> Unit) {
    val round = state.round
    Column(Modifier.fillMaxSize().padding(20.dp)) {
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
        Spacer(Modifier.height(24.dp))

        if (!round.isOver) {
            LetterGrid(guessed = round.guessedLetters, answer = round.answer, onGuess = onGuess)
        } else {
            val won = round.isWon
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (won) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Text(
                    if (won) "Umeshinda! 🎉" else "Umepoteza. Jibu lilikuwa \"${round.answer}\"",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.index + 1 >= state.rounds.size) "Maliza" else "Endelea")
            }
        }
    }
}

@Composable
private fun LetterGrid(guessed: Set<Char>, answer: String, onGuess: (Char) -> Unit) {
    val rows = ALPHABET.chunked(7)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth()) {
                row.forEach { letter ->
                    val used = letter in guessed
                    val correct = used && letter in answer
                    Card(
                        onClick = { if (!used) onGuess(letter) },
                        enabled = !used,
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
