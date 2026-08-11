package com.swahilib.feature.word_search.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.WordSearchTheme
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.core.ui.components.game.StepTimerBar
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
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
    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    val isPlaying = state is WordSearchUiState.Playing
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
                is WordSearchUiState.Playing -> GameTopBar(
                    title = "Tafuta Maneno",
                    level = s.level,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )
                else -> AppTopBar(title = "Tafuta Maneno", showGoBack = true, onNavIconClick = { navController.popBackStack() })
            }
        },
        bottomBar = {
            val s = state
            if (s is WordSearchUiState.Playing && s.easyMode) {
                LetterPoolBar(letters = s.letterPool, highlighted = s.highlightedLetter, onLetter = viewModel::tapPoolLetter)
            }
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
                is WordSearchUiState.LevelSelect -> LevelSelectContent(s.previousPoints, s.levels, viewModel::chooseLevel)
                is WordSearchUiState.Playing -> PlayingContent(state = s, onTapCell = viewModel::tapCell, onGiveUp = viewModel::giveUp)
                is WordSearchUiState.Finished -> FinishedContent(
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
private fun PlayingContent(state: WordSearchUiState.Playing, onTapCell: (Int, Int) -> Unit, onGiveUp: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        // Grid stays fixed; the word list floats over the lower part of it as a scrollable, semi-transparent panel.
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            StepTimerBar(remainingSeconds = state.secondsRemaining, totalSeconds = state.secondsTotal)
            Spacer(Modifier.height(8.dp))
            Text(
                "Gusa herufi ya kwanza, kisha herufi ya mwisho, ya kila neno.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.lastMissed) {
                Text("Hakuna neno hapo - jaribu tena.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                WordSearchGrid(state, onTapCell)
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.36f),
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
                Text("Maneno ya Kutafuta", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.words, key = { it.word }) { word -> WordListRow(word) }
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = onGiveUp, modifier = Modifier.fillMaxWidth()) { Text("Maliza / Toa Mchezo") }
            }
        }
    }
}

@Composable
private fun WordListRow(word: PlacedWord) {
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
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun LetterPoolBar(letters: List<Char>, highlighted: Char?, onLetter: (Char) -> Unit) {
    Surface(tonalElevation = 6.dp) {
        Column {
            Text(
                "Gusa herufi kuiona kwenye gridi",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                letters.forEach { letter ->
                    Card(
                        onClick = { onLetter(letter) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (letter == highlighted) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                        ),
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(letter.uppercase(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WordSearchGrid(state: WordSearchUiState.Playing, onTapCell: (Int, Int) -> Unit) {
    val cellSize = (300 / state.puzzle.size.coerceAtLeast(1)).coerceIn(16, 28).dp
    Column {
        for (row in state.puzzle.grid.indices) {
            Row {
                for (col in state.puzzle.grid[row].indices) {
                    val selected = state.selectionStart == row to col
                    val letter = state.puzzle.grid[row][col]
                    val highlighted = state.highlightedLetter != null && letter == state.highlightedLetter
                    Box(
                        Modifier
                            .size(cellSize)
                            .clip(CircleShape)
                            .background(
                                when {
                                    selected -> MaterialTheme.colorScheme.primaryContainer
                                    highlighted -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable { onTapCell(row, col) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            letter.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinishedContent(state: WordSearchUiState.Finished, onPlayAgain: () -> Unit, onDone: () -> Unit) {
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
