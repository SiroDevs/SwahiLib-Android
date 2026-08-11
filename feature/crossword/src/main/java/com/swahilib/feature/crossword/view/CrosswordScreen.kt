package com.swahilib.feature.crossword.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.onFocusChanged
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
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.core.ui.components.game.StepTimerBar
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
    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    val isPlaying = state is CrosswordUiState.Playing
    BackHandler(enabled = isPlaying) { showExit = true }

    if (showRestart) {
        GameRestartDialog(onConfirm = { showRestart = false; viewModel.restart() }, onDismiss = { showRestart = false })
    }
    if (showExit) {
        GameExitDialog(
            onGoBackDiscard = { showExit = false; viewModel.discardAndExit { navController.popBackStack() } },
            onSaveAndGoBack = { showExit = false; viewModel.saveAndExit { navController.popBackStack() } },
            onCancel = { showExit = false },
        )
    }

    Scaffold(
        topBar = {
            when (val s = state) {
                is CrosswordUiState.Playing -> GameTopBar(
                    title = "CrossWord",
                    level = s.level,
                    previousPoints = s.previousPoints,
                    livePoints = 0,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )
                else -> AppTopBar(title = "CrossWord", showGoBack = true, onNavIconClick = { navController.popBackStack() })
            }
        },
        bottomBar = {
            val s = state
            if (s is CrosswordUiState.Playing && s.easyMode) {
                LetterPoolBar(letters = s.letterPool, onLetter = viewModel::tapPoolLetter, onBackspace = viewModel::poolBackspace)
            }
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
                is CrosswordUiState.LevelSelect -> LevelSelectContent(s.previousPoints, s.levels, viewModel::chooseLevel)
                is CrosswordUiState.Playing -> PlayingContent(
                    state = s,
                    onAnswerChange = viewModel::updateAnswer,
                    onFocus = viewModel::focusEntry,
                    onFinish = viewModel::finishNow,
                )
                is CrosswordUiState.Finished -> FinishedContent(
                    state = s,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun LevelSelectContent(previousPoints: Int, levels: List<GameLevelUiModel>, onLevelTap: (GameLevelUiModel) -> Unit) {
    Column(Modifier.fillMaxSize().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Chagua Kiwango", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(4.dp))
        Text("Jumla ya alama: $previousPoints", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        LevelCarousel(levels = levels, onLevelTap = onLevelTap)
    }
}

@Composable
private fun PlayingContent(
    state: CrosswordUiState.Playing,
    onAnswerChange: (String, String) -> Unit,
    onFocus: (String) -> Unit,
    onFinish: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        // The grid is fixed - it never scrolls, per design. Center it in the space above the clue overlay.
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            StepTimerBar(remainingSeconds = state.secondsRemaining, totalSeconds = state.secondsTotal)
            Spacer(Modifier.height(12.dp))
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                CrosswordGrid(state.puzzle, state.answers, state.focusedEntryId)
            }
        }

        // Clues float as a scrollable, semi-transparent panel over the lower part of the grid.
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.42f),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.93f),
            tonalElevation = 4.dp,
        ) {
            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small),
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    val byDirection = state.puzzle.entries.groupBy { it.direction }
                    byDirection[CrosswordDirection.ACROSS]?.sortedBy { it.number }?.let { entries ->
                        item { ClueSectionHeader("Mlalo (Across)") }
                        items(entries) { entry -> ClueRow(entry, state.answers[entry.id].orEmpty(), state.focusedEntryId == entry.id, state.easyMode, onFocus, onAnswerChange) }
                    }
                    byDirection[CrosswordDirection.DOWN]?.sortedBy { it.number }?.let { entries ->
                        item { ClueSectionHeader("Wima (Down)") }
                        items(entries) { entry -> ClueRow(entry, state.answers[entry.id].orEmpty(), state.focusedEntryId == entry.id, state.easyMode, onFocus, onAnswerChange) }
                    }
                    val diagonals = (byDirection[CrosswordDirection.DIAGONAL_DOWN_RIGHT].orEmpty() + byDirection[CrosswordDirection.DIAGONAL_DOWN_LEFT].orEmpty())
                        .sortedBy { it.number }
                    if (diagonals.isNotEmpty()) {
                        item { ClueSectionHeader("Mshazari (Diagonal)") }
                        items(diagonals) { entry -> ClueRow(entry, state.answers[entry.id].orEmpty(), state.focusedEntryId == entry.id, state.easyMode, onFocus, onAnswerChange) }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("Maliza") }
            }
        }
    }
}

@Composable
private fun ClueSectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
}

@Composable
private fun ClueRow(
    entry: CrosswordEntry,
    typed: String,
    focused: Boolean,
    easyMode: Boolean,
    onFocus: (String) -> Unit,
    onAnswerChange: (String, String) -> Unit,
) {
    OutlinedTextField(
        value = typed,
        onValueChange = { if (!easyMode) onAnswerChange(entry.id, it) },
        label = { Text("${entry.number}. ${entry.clue}") },
        singleLine = true,
        readOnly = easyMode,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused) onFocus(entry.id) }
            .let { if (easyMode) it.clickable { onFocus(entry.id) } else it }
            .let { if (focused) it.border(1.5.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small) else it },
    )
}

@Composable
private fun LetterPoolBar(letters: List<Char>, onLetter: (Char) -> Unit, onBackspace: () -> Unit) {
    Surface(tonalElevation = 6.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            letters.forEach { letter ->
                Card(
                    onClick = { onLetter(letter) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(letter.uppercase(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
            IconButton(onClick = onBackspace) {
                Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Futa herufi")
            }
        }
    }
}

/** True if the entry passes through (row, col) - generalized over every direction, including diagonals. */
private fun CrosswordEntry.covers(row: Int, col: Int): Int? {
    for (i in answer.indices) {
        if (this.row + i * direction.dRow == row && this.col + i * direction.dCol == col) return i
    }
    return null
}

@Composable
private fun CrosswordGrid(puzzle: CrosswordPuzzle, answers: Map<String, String>, focusedEntryId: String?) {
    val cellSize = (280 / puzzle.cols.coerceAtLeast(1)).coerceIn(20, 34).dp

    Column(
        Modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        for (row in 0 until puzzle.rows) {
            Row {
                for (col in 0 until puzzle.cols) {
                    val cell = puzzle.cells[row to col]
                    if (cell == null) {
                        Box(Modifier.size(cellSize).background(MaterialTheme.colorScheme.surfaceVariant))
                    } else {
                        val coveringOffsets = puzzle.entries.mapNotNull { entry -> entry.covers(row, col)?.let { entry to it } }
                        val focusedHere = focusedEntryId != null && coveringOffsets.any { it.first.id == focusedEntryId }
                        val typedChar = coveringOffsets.firstNotNullOfOrNull { (entry, offset) -> answers[entry.id]?.getOrNull(offset) }
                        val background = if (focusedHere) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        Box(
                            Modifier
                                .size(cellSize)
                                .background(background)
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            cell.number?.let {
                                Text(it.toString(), fontSize = 8.sp, modifier = Modifier.padding(1.dp))
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

@Composable
private fun FinishedContent(state: CrosswordUiState.Finished, onPlayAgain: () -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (state.result.isPerfect) "\ud83c\udf89 Msalaba Kamili!" else "Umemaliza!",
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
