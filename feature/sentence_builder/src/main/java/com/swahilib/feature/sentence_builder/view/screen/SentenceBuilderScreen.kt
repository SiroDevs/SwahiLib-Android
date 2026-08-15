package com.swahilib.feature.sentence_builder.view.screen

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
import com.swahilib.feature.sentence_builder.utils.SentenceUiState
import com.swahilib.feature.sentence_builder.view.components.PlayingContent
import com.swahilib.feature.sentence_builder.view.components.FinishedContent
import com.swahilib.feature.sentence_builder.viewmodel.SentenceBuilderViewModel
import kotlin.collections.get

private val SENTENCE_INSTRUCTIONS = listOf(
    "Panga maneno yaliyochanganyika kuunda sentensi sahihi ya Kiswahili.",
    "Gusa neno kuliongeza; gusa 'Futa' kuanza upya mzunguko huo huo.",
    "Kila kiwango kina muda maalum kwa kila sentensi - ukiisha muda, mchezo utaendelea kiotomatiki.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentenceBuilderScreen(
    navController: NavHostController,
    viewModel: SentenceBuilderViewModel,
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
    val isPlaying = state is SentenceUiState.Playing
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
                is SentenceUiState.Playing -> GameTopBar(
                    title = "Panga Sentensi",
                    level = s.level,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                    soundPlayer = viewModel.soundPlayer,
                )

                else -> AppTopBar(
                    title = "Panga Sentensi",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                is SentenceUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is SentenceUiState.Empty -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Hakuna methali za kutosha kwa sasa.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is SentenceUiState.Overview -> GameOverviewScreen(
                    title = "Panga Sentensi",
                    tagline = "Panga maneno kwa mpangilio sahihi kuunda sentensi.",
                    instructions = SENTENCE_INSTRUCTIONS,
                    onStart = viewModel::proceedToLevelSelect,
                    onPractice = viewModel::startPractice,
                )

                is SentenceUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = { model -> viewModel.chooseLevel(model.level) },
                )

                is SentenceUiState.Playing -> AnimatedContent(
                    targetState = s,
                    contentKey = { it.index },
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "sentenceRound",
                ) { playingState ->
                    PlayingContent(
                        state = playingState,
                        onPick = viewModel::pickWord,
                        onClear = viewModel::clear,
                        onSubmit = viewModel::submit,
                        onTogglePause = viewModel::togglePause,
                        onContinue = viewModel::continueToNext,
                    )
                }

                is SentenceUiState.Finished -> FinishedContent(
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
