package com.swahilib.feature.crossword.view.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.crossword.utils.CrosswordUiState
import kotlin.text.orEmpty

@Composable
fun FinishedContent(state: CrosswordUiState.Finished, onPlayAgain: () -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (state.result.isPerfect) "\ud83c\udf89 Crossword Kamili!" else "Umemaliza!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(12.dp))
        Text("${state.result.correctEntries}/${state.result.totalEntries} sahihi", style = MaterialTheme.typography.titleMedium)
        Text("+${state.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        if (state.level != null) {
            Text("+${state.pointsEarned} alama - Kiwango ${state.level}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(16.dp))
        AchievementUnlockBanner(state.unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

        Text("Majibu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.puzzle.entries.sortedBy { it.number }) { entry ->
                val typed = state.answers[entry.id].orEmpty()
                val correct = typed.trim().equals(entry.answer, ignoreCase = true)
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
                            Text("${entry.number}. ${entry.answer}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(entry.clue, style = MaterialTheme.typography.bodySmall)
                            if (!correct && typed.isNotBlank()) {
                                Text("Umeandika: \"$typed\"", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.level != null) {
                Button(onClick = onPlayAgain, modifier = Modifier.weight(1f)) { Text("Viwango") }
            }
            Button(onClick = onDone, modifier = Modifier.weight(1f)) { Text("Sawa") }
        }
    }
}
