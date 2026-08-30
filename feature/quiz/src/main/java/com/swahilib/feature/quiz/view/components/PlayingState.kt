package com.swahilib.feature.quiz.view.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.games.model.QuizFormat
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.ui.components.game.AndroidPauseOverlay
import com.swahilib.feature.quiz.utils.QuizUiState

@Composable
fun PlayingState(
    state: QuizUiState.Playing,
    selectedOptionId: String?,
    onSelectOption: (String) -> Unit,
    typedText: String,
    onTypedTextChange: (String) -> Unit,
    matchedPairs: Map<String, String>,
    onMatchedPairsChange: (Map<String, String>) -> Unit,
    onTogglePause: () -> Unit,
) {
    val question = state.question
    val inputEnabled = !state.answered && !state.paused

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
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

            when (question.format) {
                QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> ChoiceOptions(
                    question = question,
                    selected = selectedOptionId,
                    enabled = inputEnabled,
                    onSelect = onSelectOption,
                )
                QuizFormat.FILL_IN_BLANK -> FillInBlank(
                    text = typedText,
                    enabled = inputEnabled,
                    onTextChange = onTypedTextChange,
                )
                QuizFormat.MATCH_WORDS -> MatchWords(
                    question = question,
                    matchedPairs = matchedPairs,
                    enabled = inputEnabled,
                    onPairsChange = onMatchedPairsChange,
                )
            }
        }

        AndroidPauseOverlay(visible = state.paused, onResume = onTogglePause)
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
