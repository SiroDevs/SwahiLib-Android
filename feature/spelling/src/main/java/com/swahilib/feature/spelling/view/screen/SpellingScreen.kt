package com.swahilib.feature.spelling.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.swahilib.core.ui.components.game.GameOverviewScreen
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelCarousel
import com.swahilib.feature.spelling.utils.SpellingUiState
import com.swahilib.feature.spelling.view.components.PlayingContent
import com.swahilib.feature.spelling.view.components.FinishedContent
import com.swahilib.feature.spelling.viewmodel.SpellingViewModel

private val SPELLING_INSTRUCTIONS = listOf(
    "Soma maelezo, kisha andika neno sahihi la Kiswahili.",
    "Tumia 'Kidokezo' kuonyesha herufi moja - hupunguza alama za mzunguko huo.",
    "Kila kiwango kina muda maalum kwa kila neno; ukiisha muda, mchezo utaendelea kiotomatiki.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellingScreen(
    navController: NavHostController,
    viewModel: SpellingViewModel,
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
    val isPlaying = state is SpellingUiState.Playing
    BackHandler(enabled = isPlaying) { showExit = true }

    if (showRestart) {
        GameRestartDialog(
            onConfirm = { showRestart = false; viewModel.restart() },
            onDismiss = { showRestart = false })
    }
    if (showExit) {
        GameExitDialog(
            onGoBackDiscard = {
                showExit = false; viewModel.discardAndExit { navController.popBackStack() }
            },
            onSaveAndGoBack = {
                showExit = false; viewModel.saveAndExit { navController.popBackStack() }
            },
            onCancel = { showExit = false },
        )
    }

    Scaffold(
        topBar = {
            when (val s = state) {
                is SpellingUiState.Playing -> GameTopBar(
                    title = "Tahajia (Spellcheck)",
                    level = s.level,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                    soundPlayer = viewModel.soundPlayer,
                )

                else -> AppTopBar(
                    title = "Tahajia (Spellcheck)",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val s = state) {
                is SpellingUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is SpellingUiState.Empty -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Hakuna maneno ya kutosha kwa sasa.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is SpellingUiState.Overview -> GameOverviewScreen(
                    title = "Tahajia (Spellcheck)",
                    tagline = "Andika tahajia sahihi ya maneno ya Kiswahili.",
                    instructions = SPELLING_INSTRUCTIONS,
                    onStart = viewModel::proceedToLevelSelect,
                    onPractice = viewModel::startPractice,
                )

                is SpellingUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = { model -> viewModel.chooseLevel(model.level) },
                )

                is SpellingUiState.Playing -> AnimatedContent(
                    targetState = s,
                    contentKey = { it.index },
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "spellingRound",
                ) { playingState ->
                    PlayingContent(
                        state = playingState,
                        onHint = viewModel::useHint,
                        onSubmit = viewModel::submit
                    )
                }

                is SpellingUiState.Finished -> FinishedContent(
                    state = s,
                    soundPlayer = viewModel.soundPlayer,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun LevelSelectContent(
    previousPoints: Int,
    levels: List<GameLevelUiModel>,
    onLevelTap: (GameLevelUiModel) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Chagua Kiwango",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Jumla ya alama: $previousPoints",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        LevelCarousel(levels = levels, onLevelTap = onLevelTap)
    }
}
