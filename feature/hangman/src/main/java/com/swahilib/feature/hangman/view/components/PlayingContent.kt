package com.swahilib.feature.hangman.view.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.game.StepTimerBar
import com.swahilib.feature.hangman.utils.HangmanUiState
import kotlin.collections.chunked
import kotlin.collections.forEach

private val ALPHABET = ('A'..'Z').toList()

@Composable
fun PlayingContent(state: HangmanUiState.Playing, onGuess: (Char) -> Unit) {
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
