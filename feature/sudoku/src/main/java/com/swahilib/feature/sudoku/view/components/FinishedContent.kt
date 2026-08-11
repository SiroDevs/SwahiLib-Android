package com.swahilib.feature.sudoku.view.components

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
import com.swahilib.sudoku.viewmodel.SudokuUiState

@Composable
fun FinishedContent(state: SudokuUiState.Finished, onPlayAgain: () -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (state.result.isPerfect) "\ud83c\udf89 Umepata Maneno Yote!" else "Umemaliza!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(12.dp))
        Text("${state.result.foundWords}/${state.result.totalWords} yamepatikana", style = MaterialTheme.typography.titleMedium)
        Text("+${state.result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        if (state.level != null) {
            Text("+${state.pointsEarned} alama - Kiwango ${state.level}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(16.dp))
        AchievementUnlockBanner(state.unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.words, key = { it.word }) { word ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (word.found) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (word.found) Icons.Default.CheckCircle else Icons.Default.Close, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(word.word, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Text(word.clue, style = MaterialTheme.typography.bodySmall)
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
