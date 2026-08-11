package com.swahilib.feature.quiz.view.components

import androidx.compose.foundation.layout.Arrangement
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
import com.swahilib.core.ui.components.game.StepTimerBar
import com.swahilib.feature.quiz.utils.QuizUiState

@Composable
fun PlayingState(
    state: QuizUiState.Playing,
    onChoice: (String) -> Unit,
    onTyped: (String) -> Unit,
    onMatches: (Map<String, String>) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        StepTimerBar(remainingSeconds = state.secondsRemaining, totalSeconds = state.secondsTotal)
        Spacer(Modifier.height(16.dp))
        Text(
            state.progressLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))

        val question = state.question
        Text(
            question.prompt,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(20.dp))

        // Answers are never revealed mid-game - options just lock once tapped, no correct/wrong coloring here.
        when (question.format) {
            QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> ChoiceOptions(question, onChoice)
            QuizFormat.FILL_IN_BLANK -> FillInBlank(question, onTyped)
            QuizFormat.MATCH_WORDS -> MatchWords(question, onMatches)
        }
    }
}

@Composable
private fun ChoiceOptions(question: QuizQuestion, onChoice: (String) -> Unit) {
    var selected by remember(question.id) { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEach { option ->
            val isSelected = selected == option.id
            Card(
                colors = if (isSelected) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                } else {
                    CardDefaults.cardColors()
                },
                onClick = {
                    if (selected == null) {
                        selected = option.id
                        onChoice(option.id)
                    }
                },
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
private fun FillInBlank(question: QuizQuestion, onTyped: (String) -> Unit) {
    var text by remember(question.id) { mutableStateOf("") }
    var submitted by remember(question.id) { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = { if (!submitted) text = it },
        label = { Text("Jibu lako") },
        singleLine = true,
        enabled = !submitted,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    Button(
        onClick = { submitted = true; onTyped(text) },
        enabled = !submitted && text.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Tuma Jibu")
    }
}
