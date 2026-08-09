package com.swahilib.feature.crossword.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.CrosswordDirection
import com.swahilib.core.games.model.CrosswordEntry
import com.swahilib.core.games.model.CrosswordPuzzle
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.crossword.viewmodel.CrosswordUiState
import com.swahilib.feature.crossword.viewmodel.CrosswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordScreen(
    navController: NavHostController,
    viewModel: CrosswordViewModel,
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
                title = "CrossWord",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is CrosswordUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is CrosswordUiState.Empty -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("Hakuna maneno ya kutosha kuunda msalaba kwa sasa.", style = MaterialTheme.typography.bodyLarge)
                }
                is CrosswordUiState.Playing -> PlayingContent(
                    state = s,
                    onAnswerChange = viewModel::updateAnswer,
                    onCheck = viewModel::check,
                    onFinish = viewModel::finish,
                )
                is CrosswordUiState.Finished -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        if (s.result.isPerfect) "🎉 Msalaba Kamili!" else "Umemaliza!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("${s.result.correctEntries}/${s.result.totalEntries} sahihi", style = MaterialTheme.typography.titleMedium)
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
    state: CrosswordUiState.Playing,
    onAnswerChange: (String, String) -> Unit,
    onCheck: () -> Unit,
    onFinish: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        CrosswordGrid(state.puzzle, state.answers, state.checked)
        Spacer(Modifier.height(20.dp))

        val across = state.puzzle.entries.filter { it.direction == CrosswordDirection.ACROSS }.sortedBy { it.number }
        val down = state.puzzle.entries.filter { it.direction == CrosswordDirection.DOWN }.sortedBy { it.number }

        if (across.isNotEmpty()) {
            ClueSection("Mlalo (Across)", across, state.answers, state.checked, onAnswerChange)
            Spacer(Modifier.height(16.dp))
        }
        if (down.isNotEmpty()) {
            ClueSection("Wima (Down)", down, state.answers, state.checked, onAnswerChange)
            Spacer(Modifier.height(20.dp))
        }

        if (!state.checked) {
            Button(onClick = onCheck, modifier = Modifier.fillMaxWidth()) { Text("Angalia Majibu") }
        } else {
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("Maliza") }
        }
    }
}

@Composable
private fun ClueSection(
    title: String,
    entries: List<CrosswordEntry>,
    answers: Map<String, String>,
    checked: Boolean,
    onAnswerChange: (String, String) -> Unit,
) {
    Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        entries.forEach { entry ->
            val typed = answers[entry.id].orEmpty()
            val correct = checked && typed.trim().equals(entry.answer, ignoreCase = true)
            val wrong = checked && !correct
            OutlinedTextField(
                value = typed,
                onValueChange = { if (!checked) onAnswerChange(entry.id, it) },
                label = { Text("${entry.number}. ${entry.clue}") },
                singleLine = true,
                enabled = !checked,
                isError = wrong,
                modifier = Modifier.fillMaxWidth(),
                supportingText = if (wrong) {
                    { Text("Jibu sahihi: ${entry.answer}") }
                } else null,
            )
        }
    }
}

@Composable
private fun CrosswordGrid(puzzle: CrosswordPuzzle, answers: Map<String, String>, checked: Boolean) {
    val cellSize = (280 / puzzle.cols.coerceAtLeast(1)).coerceIn(20, 36).dp

    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        for (row in 0 until puzzle.rows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0 until puzzle.cols) {
                    val cell = puzzle.cells[row to col]
                    if (cell == null) {
                        Box(Modifier.size(cellSize).background(MaterialTheme.colorScheme.surfaceVariant))
                    } else {
                        val coveringEntries = puzzle.entries.filter { entry ->
                            when (entry.direction) {
                                CrosswordDirection.ACROSS -> entry.row == row && col in entry.col until entry.col + entry.answer.length
                                CrosswordDirection.DOWN -> entry.col == col && row in entry.row until entry.row + entry.answer.length
                            }
                        }
                        val typedChar = coveringEntries.firstNotNullOfOrNull { entry ->
                            val offset = when (entry.direction) {
                                CrosswordDirection.ACROSS -> col - entry.col
                                CrosswordDirection.DOWN -> row - entry.row
                            }
                            answers[entry.id]?.getOrNull(offset)
                        }
                        val allCorrect = checked && coveringEntries.all {
                            answers[it.id]?.trim()?.equals(it.answer, ignoreCase = true) == true
                        }
                        val anyWrong = checked && coveringEntries.any {
                            val typed = answers[it.id].orEmpty()
                            typed.isNotBlank() && !typed.trim().equals(it.answer, ignoreCase = true)
                        }
                        val background = when {
                            allCorrect -> MaterialTheme.colorScheme.tertiaryContainer
                            anyWrong -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surface
                        }
                        Box(
                            Modifier
                                .size(cellSize)
                                .background(background)
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            cell.number?.let {
                                Text(
                                    it.toString(),
                                    fontSize = 8.sp,
                                    modifier = Modifier.padding(1.dp),
                                )
                            }
                            Text(
                                (typedChar ?: ' ').toString().uppercase(),
                                modifier = Modifier.fillMaxSize(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }
        }
    }
}
