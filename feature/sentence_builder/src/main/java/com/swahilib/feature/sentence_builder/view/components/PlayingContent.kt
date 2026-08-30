package com.swahilib.feature.sentence_builder.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.game.AndroidPauseOverlay
import com.swahilib.feature.sentence_builder.utils.SentenceUiState

@Composable
fun PlayingContent(
    state: SentenceUiState.Playing,
    onPick: (Int) -> Unit,
    onClear: () -> Unit,
    onTogglePause: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            if (state.practice) {
                Text("MAZOEZI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                "Sentensi ${state.index + 1}/${state.questions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Panga maneno yafuatayo kuwa sentensi sahihi:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    state.picked.joinToString(" ").ifBlank { " " },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Spacer(Modifier.height(20.dp))

            val inputEnabled = !state.locked && !state.paused
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.question.shuffledWords.forEachIndexed { index, word ->
                    val used = index in state.pickedIndices
                    Card(
                        onClick = { if (!used && inputEnabled) onPick(index) },
                        enabled = !used && inputEnabled,
                        colors = CardDefaults.cardColors(
                            containerColor = if (used) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                        ),
                        modifier = Modifier.wrapContentWidth(),
                    ) {
                        Text(word, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            OutlinedButton(onClick = onClear, enabled = inputEnabled, modifier = Modifier.fillMaxWidth()) { Text("Futa") }
        }

        AndroidPauseOverlay(visible = state.paused, onResume = onTogglePause)
    }
}
