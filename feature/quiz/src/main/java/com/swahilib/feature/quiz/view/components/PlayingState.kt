package com.swahilib.feature.quiz.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.swahilib.core.games.model.QuizFormat
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.ui.components.game.GameStatusBar
import com.swahilib.core.ui.components.game.GameSubmitContinueBar
import com.swahilib.feature.quiz.utils.QuizUiState

@Composable
fun PlayingState(
    state: QuizUiState.Playing,
    onChoice: (String) -> Unit,
    onTyped: (String) -> Unit,
    onMatches: (Map<String, String>) -> Unit,
    onTogglePause: () -> Unit,
    onContinue: () -> Unit,
) {
    val question = state.question
    var selectedOptionId by remember(question.id) { mutableStateOf<String?>(null) }
    var typedText by remember(question.id) { mutableStateOf("") }
    var matchedPairs by remember(question.id) { mutableStateOf<Map<String, String>>(emptyMap()) }

    val hasDraftAnswer = when (question.format) {
        QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> selectedOptionId != null
        QuizFormat.FILL_IN_BLANK -> typedText.isNotBlank()
        QuizFormat.MATCH_WORDS -> matchedPairs.size == question.matchLeft.size
    }
    val inputEnabled = !state.answered && !state.paused

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            GameStatusBar(
                remainingSeconds = state.secondsRemaining,
                totalSeconds = state.secondsTotal,
                previousPoints = state.previousPoints,
                livePoints = state.livePoints,
                paused = state.paused,
                onTogglePause = onTogglePause,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                state.progressLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(20.dp))

            Text(
                question.prompt,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(Modifier.height(20.dp))

            // Answers are never revealed mid-game - selecting just marks a draft choice;
            // nothing is recorded until "Wasilisha" (submit) is tapped.
            when (question.format) {
                QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> ChoiceOptions(
                    question = question,
                    selected = selectedOptionId,
                    enabled = inputEnabled,
                    onSelect = { selectedOptionId = it },
                )
                QuizFormat.FILL_IN_BLANK -> FillInBlank(
                    text = typedText,
                    enabled = inputEnabled,
                    onTextChange = { typedText = it },
                )
                QuizFormat.MATCH_WORDS -> MatchWords(
                    question = question,
                    matchedPairs = matchedPairs,
                    enabled = inputEnabled,
                    onPairsChange = { matchedPairs = it },
                )
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(16.dp))
            GameSubmitContinueBar(
                onSubmit = {
                    when (question.format) {
                        QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> selectedOptionId?.let(onChoice)
                        QuizFormat.FILL_IN_BLANK -> onTyped(typedText)
                        QuizFormat.MATCH_WORDS -> onMatches(matchedPairs)
                    }
                },
                submitEnabled = hasDraftAnswer && inputEnabled,
                onContinue = onContinue,
                continueEnabled = state.answered && !state.paused,
            )
        }

        PauseOverlay(visible = state.paused, onResume = onTogglePause)
    }
}

@Composable
private fun PauseOverlay(visible: Boolean, onResume: () -> Unit) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
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
                Button(onClick = onResume) { Text("Endelea na Mchezo") }
            }
        }
    }
}

@Composable
private fun ChoiceOptions(question: QuizQuestion, selected: String?, enabled: Boolean, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEach { option ->
            val isSelected = selected == option.id
            Card(
                colors = if (isSelected) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                } else {
                    CardDefaults.cardColors()
                },
                onClick = { if (enabled) onSelect(option.id) },
                enabled = enabled,
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(option.text, style = MaterialTheme.typography.bodyLarge)
                    if (isSelected) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun FillInBlank(text: String, enabled: Boolean, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = { if (enabled) onTextChange(it) },
        label = { Text("Jibu lako") },
        singleLine = true,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun MatchWords(
    question: QuizQuestion,
    matchedPairs: Map<String, String>,
    enabled: Boolean,
    onPairsChange: (Map<String, String>) -> Unit,
) {
    var selectedLeft by remember(question.id) { mutableStateOf<String?>(null) }

    Text(
        "Gusa neno, kisha gusa maana yake.",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.matchLeft.forEach { left ->
                MatchChip(
                    text = left.text,
                    matched = matchedPairs.containsKey(left.id),
                    selected = selectedLeft == left.id,
                    enabled = enabled && !matchedPairs.containsKey(left.id),
                    onClick = { selectedLeft = left.id },
                )
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.matchRight.forEach { right ->
                val alreadyUsed = matchedPairs.containsValue(right.id)
                MatchChip(
                    text = right.text,
                    matched = alreadyUsed,
                    selected = false,
                    enabled = enabled && !alreadyUsed && selectedLeft != null,
                    onClick = {
                        selectedLeft?.let { left ->
                            onPairsChange(matchedPairs + (left to right.id))
                            selectedLeft = null
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun MatchChip(text: String, matched: Boolean, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = when {
            matched -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            selected -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            else -> CardDefaults.cardColors()
        },
    ) {
        Text(text, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
    }
}
