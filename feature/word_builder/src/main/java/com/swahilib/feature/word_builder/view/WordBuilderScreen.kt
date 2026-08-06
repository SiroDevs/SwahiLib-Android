package com.swahilib.feature.word_builder.view

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.word_builder.viewmodel.WordBuilderUiState
import com.swahilib.feature.word_builder.viewmodel.WordBuilderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBuilderScreen(
    navController: NavHostController,
    viewModel: WordBuilderViewModel,
    challengeId: String?,
    activityId: String?,
    difficulty: Difficulty = Difficulty.BEGINNER,
    timedMode: Boolean = false,
    endless: Boolean = false,
) {
    LaunchedEffect(challengeId, activityId) {
        viewModel.start(
            challengeId = challengeId,
            activityId = activityId,
            difficulty = difficulty,
            timedMode = timedMode,
            endless = endless,
        )
    }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Jenzi la Maneno",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is WordBuilderUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is WordBuilderUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna maneno ya kutosha kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is WordBuilderUiState.Playing -> PlayingContent(
                    state = s,
                    onPick = viewModel::pickLetter,
                    onClear = viewModel::clearPicks,
                    onHint = viewModel::useHint,
                    onSubmit = viewModel::submit,
                    onNext = viewModel::next,
                    onStopEndless = viewModel::stopEndless,
                )
                is WordBuilderUiState.Finished -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        if (s.result.isPerfect) "🎉 Kamili Bila Kidokezo!" else "Umemaliza!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("${s.result.correctWords}/${s.result.totalWords} maneno sahihi", style = MaterialTheme.typography.titleMedium)
                    Text("+${s.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(24.dp))
                    AchievementUnlockBanner(
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
    state: WordBuilderUiState.Playing,
    onPick: (Int) -> Unit,
    onClear: () -> Unit,
    onHint: () -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onStopEndless: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                if (state.totalRounds != null) "Neno ${state.roundIndex + 1}/${state.totalRounds}" else "Neno #${state.roundIndex + 1}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            state.secondsRemaining?.let { seconds ->
                Text(
                    "⏱ ${seconds}s",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (seconds <= 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        if (state.word.hint.isNotBlank()) {
            Text(
                "Kidokezo: ${state.word.hint}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(24.dp))

        // Assembled word slots
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

        // Scrambled letter tiles
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            state.word.scrambledLetters.forEachIndexed { index, letter ->
                val used = index in state.pickedIndices
                LetterTile(
                    letter = letter,
                    used = used,
                    enabled = state.feedback == null && !used,
                    onClick = { onPick(index) },
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        if (state.feedback == null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Futa") }
                OutlinedButton(onClick = onHint, modifier = Modifier.weight(1f)) { Text("Kidokezo") }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onSubmit,
                enabled = state.assembled.length == state.word.answer.length,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Tuma") }
            if (state.endless) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onStopEndless, modifier = Modifier.fillMaxWidth()) { Text("Maliza Mchezo") }
            }
        } else {
            val correct = state.feedback == true
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Text(
                    if (correct) "Sahihi! 🎉" else "Jibu sahihi lilikuwa \"${state.word.answer}\"",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Endelea") }
        }
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
