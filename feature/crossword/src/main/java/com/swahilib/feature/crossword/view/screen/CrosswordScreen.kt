package com.swahilib.feature.crossword.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameOverviewScreen
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameSoundFab
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.LevelSelectContent
import com.swahilib.feature.crossword.utils.CrosswordUiState
import com.swahilib.feature.crossword.view.components.FinishedContent
import com.swahilib.feature.crossword.view.components.LetterPoolBar
import com.swahilib.feature.crossword.view.components.PlayingContent
import com.swahilib.feature.crossword.viewmodel.CrosswordViewModel

private val CROSSWORD_INSTRUCTIONS = listOf(
    "Jaza majibu ya maswali ya Mlalo, Wima, na Mshazari kwenye gridi.",
    "Gusa swali kulichagua, kisha andika jibu lako - majibu hayaonyeshwi hadi umalize.",
    "Kiwango depth saa moja kwa mchezo mzima wa gridi, si kwa kila swali peke yake.",
)

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
                is CrosswordUiState.Playing -> GameTopBar(
                    title = "CrossWord",
                    level = s.level,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )

                else -> AppTopBar(
                    title = "CrossWord",
                    showGoBack = true,
                    onNavIconClick = { navController.popBackStack() })
            }
        },
        floatingActionButton = {
            if (isPlaying) GameSoundFab(viewModel.soundPlayer)
        },
        bottomBar = {
            val s = state
            if (s is CrosswordUiState.Playing && s.easyMode) {
                LetterPoolBar(
                    letters = s.letterPool,
                    onLetter = viewModel::tapPoolLetter,
                    onBackspace = viewModel::poolBackspace
                )
            }
        },
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (val s = state) {
                is CrosswordUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is CrosswordUiState.Empty -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Hakuna maneno ya kutosha kuunda Crossword kwa sasa.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                is CrosswordUiState.Overview -> GameOverviewScreen(
                    title = "CrossWord",
                    tagline = "Jaza gridi ya maneno mtambuka ya Kiswahili.",
                    instructions = CROSSWORD_INSTRUCTIONS,
                    onStart = viewModel::proceedToLevelSelect,
                    onPractice = viewModel::startPractice,
                )

                is CrosswordUiState.LevelSelect -> LevelSelectContent(
                    previousPoints = s.previousPoints,
                    levels = s.levels,
                    onLevelTap = { model -> viewModel.chooseLevel(model.level) },
                )

                is CrosswordUiState.Playing -> PlayingContent(
                    state = s,
                    onAnswerChange = viewModel::updateAnswer,
                    onFocus = viewModel::focusEntry,
                    onFinish = viewModel::finishNow,
                    onTogglePause = viewModel::togglePause,
                )

                is CrosswordUiState.Finished -> FinishedContent(
                    state = s,
                    soundPlayer = viewModel.soundPlayer,
                    onPlayAgain = { viewModel.backToLevelSelect() },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}
