package com.swahilib.feature.spelling.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.game.GameStatusBar
import com.swahilib.core.ui.components.game.GameSubmitContinueBar
import com.swahilib.feature.spelling.utils.SpellingUiState

@Composable
fun PlayingContent(
    state: SpellingUiState.Playing,
    onHint: () -> Unit,
    onSubmit: (String) -> Unit,
    onTogglePause: () -> Unit,
    onContinue: () -> Unit,
) {
    var typed by remember(state.question.id) { mutableStateOf("") }
    val inputEnabled = !state.locked && !state.paused

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            GameStatusBar(
                remainingSeconds = state.secondsRemaining,
                totalSeconds = state.secondsTotal,
                previousPoints = state.previousPoints,
                livePoints = state.livePoints,
                paused = state.paused,
                onTogglePause = onTogglePause,
            )
            Spacer(Modifier.height(16.dp))
            if (state.practice) {
                Text("MAZOEZI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(4.dp))
            }
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
                onValueChange = { if (inputEnabled) typed = it },
                label = { Text("Jibu lako") },
                singleLine = true,
                enabled = inputEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            OutlinedButton(onClick = onHint, enabled = inputEnabled, modifier = Modifier.fillMaxWidth()) { Text("Kidokezo") }
            Spacer(Modifier.weight(1f))
            GameSubmitContinueBar(
                onSubmit = { onSubmit(typed) },
                submitEnabled = inputEnabled && typed.isNotBlank(),
                onContinue = onContinue,
                continueEnabled = state.locked && !state.paused,
            )
        }

        AnimatedVisibility(visible = state.paused, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Mchezo Umesimamishwa",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onTogglePause) { Text("Endelea na Mchezo") }
                }
            }
        }
    }
}
