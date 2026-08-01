package com.swahilib.feature.word_search.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.WordSearchTheme
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.word_search.viewmodel.WordSearchUiState
import com.swahilib.feature.word_search.viewmodel.WordSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSearchScreen(
    navController: NavHostController,
    viewModel: WordSearchViewModel,
    challengeId: String?,
    activityId: String?,
    difficulty: Difficulty = Difficulty.BEGINNER,
    theme: WordSearchTheme = WordSearchTheme.RANDOM,
) {
    LaunchedEffect(challengeId, activityId) {
        viewModel.start(challengeId = challengeId, activityId = activityId, difficulty = difficulty, theme = theme)
    }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tafuta Maneno",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is WordSearchUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is WordSearchUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna maneno ya kutosha kwa mchezo huu kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is WordSearchUiState.Playing -> PlayingContent(
                    state = s,
                    onTapCell = viewModel::tapCell,
                    onGiveUp = viewModel::giveUp,
                )
                is WordSearchUiState.Finished -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        if (s.result.isPerfect) "🎉 Umepata Maneno Yote!" else "Umemaliza!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("${s.result.foundWords}/${s.result.totalWords} yamepatikana", style = MaterialTheme.typography.titleMedium)
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
    state: WordSearchUiState.Playing,
    onTapCell: (Int, Int) -> Unit,
    onGiveUp: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            "Gusa herufi ya kwanza, kisha herufi ya mwisho, ya kila neno.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        if (state.lastMissed) {
            Text(
                "Hakuna neno hapo - jaribu tena.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(8.dp))
        }

        val cellSize = (300 / state.puzzle.size.coerceAtLeast(1)).coerceIn(18, 30).dp
        Column {
            for (row in state.puzzle.grid.indices) {
                Row {
                    for (col in state.puzzle.grid[row].indices) {
                        val selected = state.selectionStart == row to col
                        Box(
                            Modifier
                                .size(cellSize)
                                .clip(CircleShape)
                                .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                                .clickable { onTapCell(row, col) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                state.puzzle.grid[row][col].toString(),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        Text("Maneno ya Kutafuta", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.words.forEach { word ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (word.found) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                ) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                word.word,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (word.found) TextDecoration.LineThrough else null,
                                ),
                            )
                            Text(word.clue, style = MaterialTheme.typography.bodySmall)
                        }
                        if (word.found) {
                            Text("✓", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        OutlinedButton(onClick = onGiveUp, modifier = Modifier.fillMaxWidth()) {
            Text("Maliza / Toa Mchezo")
        }
    }
}
