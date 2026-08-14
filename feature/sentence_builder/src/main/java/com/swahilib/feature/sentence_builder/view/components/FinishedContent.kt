package com.swahilib.feature.sentence_builder.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.swahilib.core.games.model.SentenceQuestion
import com.swahilib.core.ui.components.game.CelebrationOverlay
import com.swahilib.core.ui.components.game.GameActionFab
import com.swahilib.core.ui.components.game.GameSoundPlayer
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.sentence_builder.utils.SentenceUiState

@Composable
fun FinishedContent(state: SentenceUiState.Finished, soundPlayer: GameSoundPlayer, onPlayAgain: () -> Unit, onDone: () -> Unit) {
    var celebrating by remember(state) { mutableStateOf(true) }
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (state.practice) {
                Text("MAZOEZI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                if (state.result.isPerfect) "\ud83c\udf89 Umepanga Kila Sentensi Sahihi!" else "Umemaliza!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(12.dp))
            Text("${state.result.correctAnswers}/${state.result.totalQuestions} sahihi", style = MaterialTheme.typography.titleMedium)
            if (!state.practice) {
                Text("+${state.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                if (state.level != null) {
                    Text("+${state.pointsEarned} alama - Kiwango ${state.level}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                }
            }
            Spacer(Modifier.height(16.dp))
            AchievementUnlockBanner(state.unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

            Text("Majibu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp))
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.questions.size) { i ->
                    SentenceReviewRow(state.questions[i], state.correctness.getOrNull(i) == true)
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.level != null) {
                    OutlinedButton(onClick = { celebrating = false; onPlayAgain() }, modifier = Modifier.weight(1f)) { Text("Viwango") }
                }
                GameActionFab(text = "Sawa", onClick = { celebrating = false; onDone() }, modifier = Modifier.weight(1f), isContinue = true)
            }
        }
        CelebrationOverlay(visible = celebrating, onDismiss = { celebrating = false }, soundPlayer = soundPlayer)
    }
}

@Composable
private fun SentenceReviewRow(question: SentenceQuestion, correct: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (correct) Icons.Default.CheckCircle else Icons.Default.Close, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(question.correctSentence, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                if (!correct) Text(question.explanation, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
