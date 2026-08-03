package com.swahilib.feature.sentence_builder.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.swahilib.feature.sentence_builder.viewmodel.SentenceBuilderViewModel
import com.swahilib.feature.sentence_builder.viewmodel.SentenceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceBuilderScreen(
    navController: NavHostController,
    viewModel: SentenceBuilderViewModel,
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
                title = "Panga Sentensi",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is SentenceUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is SentenceUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna methali za kutosha kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is SentenceUiState.Playing -> PlayingContent(
                    state = s,
                    onPick = viewModel::pickWord,
                    onClear = viewModel::clear,
                    onSubmit = viewModel::submit,
                    onNext = viewModel::next,
                )
                is SentenceUiState.Finished -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        if (s.result.isPerfect) "🎉 Umepanga Kila Sentensi Sahihi!" else "Umemaliza!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("${s.result.correctAnswers}/${s.result.totalQuestions} sahihi", style = MaterialTheme.typography.titleMedium)
                    Text("+${s.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(24.dp))
                    com.swahilib.core.ui.components.general.AchievementUnlockBanner(
                        s.unlockedAchievements,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
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
    state: SentenceUiState.Playing,
    onPick: (Int) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
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

        // Assembled sentence
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

        // Word chips
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.question.shuffledWords.forEachIndexed { index, word ->
                val used = index in state.pickedIndices
                Card(
                    onClick = { if (!used && state.feedback == null) onPick(index) },
                    enabled = !used && state.feedback == null,
                    colors = CardDefaults.cardColors(
                        containerColor = if (used) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.wrapContentWidth(),
                ) {
                    Text(
                        word,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        if (state.feedback == null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Futa") }
                Button(
                    onClick = onSubmit,
                    enabled = state.pickedIndices.size == state.question.shuffledWords.size,
                    modifier = Modifier.weight(1f),
                ) { Text("Tuma") }
            }
        } else {
            val correct = state.feedback == true
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        if (correct) "Sahihi! 🎉" else "Sentensi sahihi ni: \"${state.question.correctSentence}\"",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                    if (!correct) {
                        Spacer(Modifier.height(6.dp))
                        Text(state.question.explanation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.index + 1 >= state.questions.size) "Maliza" else "Endelea")
            }
        }
    }
}
