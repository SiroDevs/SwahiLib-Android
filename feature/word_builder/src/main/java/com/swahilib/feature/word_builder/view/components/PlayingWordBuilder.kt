package com.swahilib.feature.word_builder.view.components

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.game.AndroidPauseOverlay
import com.swahilib.feature.word_builder.utils.WordBuilderUiState

@Composable
fun PlayingWordBuilder(
    state: WordBuilderUiState.Playing,
    onPick: (Int) -> Unit,
    onClear: () -> Unit,
    onHint: () -> Unit,
    onTogglePause: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            if (state.practice) {
                Text("MAZOEZI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                "Neno ${state.roundIndex + 1}/${state.totalRounds}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(8.dp))
            if (state.word.hint.isNotBlank()) {
                Text(
                    state.word.hint,
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
                    LetterTile(letter = letter, used = used, enabled = !state.locked && !state.paused && !used, onClick = { onPick(index) })
                }
            }
            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onClear, enabled = !state.locked && !state.paused, modifier = Modifier.weight(1f)) { Text("Futa") }
                OutlinedButton(onClick = onHint, enabled = !state.locked && !state.paused, modifier = Modifier.weight(1f)) { Text("Kidokezo") }
            }
        }

        AndroidPauseOverlay(visible = state.paused, onResume = onTogglePause)
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
