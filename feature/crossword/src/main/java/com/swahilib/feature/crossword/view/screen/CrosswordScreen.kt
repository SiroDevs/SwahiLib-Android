package com.swahilib.feature.crossword.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.feature.crossword.utils.CrosswordUiState
import com.swahilib.feature.crossword.view.components.FinishedContent
import com.swahilib.feature.crossword.view.components.PlayingContent
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
                    Text("Hakuna maneno ya kutosha kuunda Crossword kwa sasa.", style = MaterialTheme.typography.bodyLarge)
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
