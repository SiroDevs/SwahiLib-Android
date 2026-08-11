package com.swahilib.feature.quiz.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.OutlinedTextField
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
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.QuizAnswer
import com.swahilib.core.games.model.QuizFormat
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizResult
import com.swahilib.core.games.model.QuizSet
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.game.GameExitDialog
import com.swahilib.core.ui.components.game.GameRestartDialog
import com.swahilib.core.ui.components.game.GameTopBar
import com.swahilib.core.ui.components.game.StepTimerBar
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner
import com.swahilib.feature.quiz.viewmodel.QuizContentSource
import com.swahilib.feature.quiz.viewmodel.QuizUiState
import com.swahilib.feature.quiz.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavHostController,
    viewModel: QuizViewModel,
    challengeId: String?,
    activityId: String?,
    difficulty: Difficulty = Difficulty.BEGINNER,
    source: QuizContentSource = QuizContentSource.WORDS,
) {
    LaunchedEffect(challengeId, activityId) {
        viewModel.start(challengeId = challengeId, activityId = activityId, difficulty = difficulty, source = source)
    }
    val state by viewModel.uiState.collectAsState()
    val title = if (source == QuizContentSource.PROVERBS) "Changamoto ya Methali" else "Jaribio la Msamiati"

    var showRestart by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    val isPlaying = state is QuizUiState.Playing
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
                is QuizUiState.Playing -> GameTopBar(
                    title = title,
                    level = null,
                    previousPoints = s.previousPoints,
                    livePoints = s.livePoints,
                    onBack = { showExit = true },
                    onRefresh = { showRestart = true },
                )
                else -> AppTopBar(title = title, showGoBack = true, onNavIconClick = { navController.popBackStack() })
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is QuizUiState.Loading -> LoadingState()
                is QuizUiState.Empty -> EmptyState()
                is QuizUiState.Playing -> AnimatedContent(
                    targetState = s.index,
                    transitionSpec = { (fadeIn(tween(220))) togetherWith (fadeOut(tween(160))) },
                    label = "quizQuestion",
                ) {
                    PlayingState(
                        state = s,
                        onChoice = viewModel::submitChoice,
                        onTyped = viewModel::submitTyped,
                        onMatches = viewModel::submitMatches,
                    )
                }
                is QuizUiState.Finished -> ResultState(
                    result = s.result,
                    quizSet = s.quizSet,
                    answers = s.answers,
                    unlockedAchievements = s.unlockedAchievements,
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            "Hakuna maneno ya kutosha kwenye kamusi kuunda jaribio kwa sasa.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PlayingState(
    state: QuizUiState.Playing,
    onChoice: (String) -> Unit,
    onTyped: (String) -> Unit,
    onMatches: (Map<String, String>) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        StepTimerBar(remainingSeconds = state.secondsRemaining, totalSeconds = state.secondsTotal)
        Spacer(Modifier.height(16.dp))
        Text(
            state.progressLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(20.dp))

        val question = state.question
        Text(
            question.prompt,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(20.dp))

        // Answers are never revealed mid-game - options just lock once tapped, no correct/wrong coloring here.
        when (question.format) {
            QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE -> ChoiceOptions(question, onChoice)
            QuizFormat.FILL_IN_BLANK -> FillInBlank(question, onTyped)
            QuizFormat.MATCH_WORDS -> MatchWords(question, onMatches)
        }
    }
}

@Composable
private fun ChoiceOptions(question: QuizQuestion, onChoice: (String) -> Unit) {
    var selected by remember(question.id) { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEach { option ->
            val isSelected = selected == option.id
            Card(
                colors = if (isSelected) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                } else {
                    CardDefaults.cardColors()
                },
                onClick = {
                    if (selected == null) {
                        selected = option.id
                        onChoice(option.id)
                    }
                },
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(option.text, style = MaterialTheme.typography.bodyLarge)
                    if (isSelected) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun FillInBlank(question: QuizQuestion, onTyped: (String) -> Unit) {
    var text by remember(question.id) { mutableStateOf("") }
    var submitted by remember(question.id) { mutableStateOf(false) }

    OutlinedTextField(
        value = text,
        onValueChange = { if (!submitted) text = it },
        label = { Text("Jibu lako") },
        singleLine = true,
        enabled = !submitted,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    Button(
        onClick = { submitted = true; onTyped(text) },
        enabled = !submitted && text.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Tuma Jibu")
    }
}

@Composable
private fun MatchWords(question: QuizQuestion, onMatches: (Map<String, String>) -> Unit) {
    var selectedLeft by remember(question.id) { mutableStateOf<String?>(null) }
    var pairs by remember(question.id) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var submitted by remember(question.id) { mutableStateOf(false) }

    Text(
        "Gusa neno, kisha gusa maana yake.",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.matchLeft.forEach { left ->
                MatchChip(
                    text = left.text,
                    matched = pairs.containsKey(left.id),
                    selected = selectedLeft == left.id,
                    enabled = !submitted && !pairs.containsKey(left.id),
                    onClick = { selectedLeft = left.id },
                )
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.matchRight.forEach { right ->
                val alreadyUsed = pairs.containsValue(right.id)
                MatchChip(
                    text = right.text,
                    matched = alreadyUsed,
                    selected = false,
                    enabled = !submitted && !alreadyUsed && selectedLeft != null,
                    onClick = {
                        selectedLeft?.let { left ->
                            pairs = pairs + (left to right.id)
                            selectedLeft = null
                            if (pairs.size == question.matchLeft.size) {
                                submitted = true
                                onMatches(pairs)
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun MatchChip(text: String, matched: Boolean, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        enabled = enabled,
        colors = when {
            matched -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            selected -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            else -> CardDefaults.cardColors()
        },
    ) {
        Text(text, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ResultState(
    result: QuizResult,
    quizSet: QuizSet,
    answers: List<QuizAnswer>,
    unlockedAchievements: List<Achievement>,
    onDone: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (result.isPerfect) "\ud83c\udf89 Umepata Alama Kamili!" else "Umemaliza Jaribio!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${result.correctAnswers}/${result.totalQuestions}",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    "Usahihi: ${(result.accuracy * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text("+${result.xpEarned} XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(16.dp))
        AchievementUnlockBanner(unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

        Text("Majibu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(quizSet.questions) { question ->
                val answer = answers.firstOrNull { it.questionId == question.id }
                QuizReviewRow(question, answer)
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text("Sawa")
        }
    }
}

@Composable
private fun QuizReviewRow(question: QuizQuestion, answer: QuizAnswer?) {
    val correct = answer?.correct == true
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (correct) Icons.Filled.CheckCircle else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(question.prompt, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(question.explanation, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
