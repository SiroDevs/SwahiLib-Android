package com.swahilib.feature.spelling.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.spelling.viewmodel.SpellingUiState
import com.swahilib.feature.spelling.viewmodel.SpellingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellingScreen(
    navController: NavHostController,
    viewModel: SpellingViewModel,
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
            AppTopBar(
                title = "Changamoto ya Tahajia",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is SpellingUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is SpellingUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna maneno ya kutosha kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is SpellingUiState.Playing -> PlayingContent(
                    state = s,
                    onHint = viewModel::useHint,
                    onSubmit = viewModel::submit,
                    onNext = viewModel::next,
                )
                is SpellingUiState.Finished -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        if (s.result.isPerfect) "🎉 Tahajia Kamili!" else "Umemaliza!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("${s.result.fullyCorrectCount}/${s.result.totalQuestions} sahihi kabisa", style = MaterialTheme.typography.titleMedium)
                    Text("Wastani wa usahihi: ${(s.result.averageCredit * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
                    Text("+${s.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Sawa")
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayingContent(
    state: SpellingUiState.Playing,
    onHint: () -> Unit,
    onSubmit: (String) -> Unit,
    onNext: () -> Unit,
) {
    var typed by remember(state.question.id) { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
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
                "Kidokezo: ${state.hintText}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = typed,
            onValueChange = { if (state.lastResult == null) typed = it },
            label = { Text("Jibu lako") },
            singleLine = true,
            enabled = state.lastResult == null,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        if (state.lastResult == null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onHint, modifier = Modifier.weight(1f)) { Text("Kidokezo") }
                Button(onClick = { onSubmit(typed) }, enabled = typed.isNotBlank(), modifier = Modifier.weight(1f)) {
                    Text("Tuma")
                }
            }
        } else {
            val result = state.lastResult
            val container = when {
                result.fullyCorrect -> MaterialTheme.colorScheme.tertiaryContainer
                result.partialCredit > 0f -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.errorContainer
            }
            Card(colors = CardDefaults.cardColors(containerColor = container)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        when {
                            result.fullyCorrect -> "Sahihi kabisa! 🎉"
                            result.partialCredit > 0f -> "Karibu sahihi (${(result.partialCredit * 100).toInt()}%)"
                            else -> "Sio sahihi"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Text("Jibu sahihi: \"${state.question.answer}\"", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.index + 1 >= state.questions.size) "Maliza" else "Endelea")
            }
        }
    }
}
