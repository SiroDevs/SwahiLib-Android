package com.swahilib.feature.quiz.view

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
import androidx.compose.material3.LinearProgressIndicator
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
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.QuizAnswer
import com.swahilib.core.games.model.QuizFormat
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizResult
import com.swahilib.core.ui.components.action.AppTopBar
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

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (source == QuizContentSource.PROVERBS) "Changamoto ya Methali" else "Jaribio la Msamiati",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is QuizUiState.Loading -> LoadingState()
                is QuizUiState.Empty -> EmptyState()
                is QuizUiState.Playing -> PlayingState(
                    state = s,
                    onChoice = viewModel::submitChoice,
                    onTyped = viewModel::submitTyped,
                    onMatches = viewModel::submitMatches,
                    onNext = viewModel::next,
                )
                is QuizUiState.Finished -> ResultState(
                    result = s.result,
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
    onNext: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(
            state.progressLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (state.index + 1f) / state.quizSet.questions.size },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))

        val question = state.question
        Text(
            question.prompt,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(20.dp))

        when (question.format) {
            QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE ->
                ChoiceOptions(question, state.lastAnswer, onChoice)
            QuizFormat.FILL_IN_BLANK ->
                FillInBlank(question, state.lastAnswer, onTyped)
            QuizFormat.MATCH_WORDS ->
                MatchWords(question, state.lastAnswer, onMatches)
        }

        state.lastAnswer?.let { answer ->
            Spacer(Modifier.height(16.dp))
            FeedbackCard(correct = answer.correct, explanation = question.explanation)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.index + 1 >= state.quizSet.questions.size) "Maliza" else "Endelea")
            }
        }
    }
}

@Composable
private fun ChoiceOptions(
    question: QuizQuestion,
    lastAnswer: QuizAnswer?,
    onChoice: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEach { option ->
            val answered = lastAnswer != null
            val isCorrectOption = option.id in question.correctOptionIds
            val isSelected = lastAnswer?.selectedOptionIds?.contains(option.id) == true
            val colors = when {
                !answered -> CardDefaults.cardColors()
                isCorrectOption -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                isSelected -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                else -> CardDefaults.cardColors()
            }
            Card(
                colors = colors,
                onClick = { if (!answered) onChoice(option.id) },
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(option.text, style = MaterialTheme.typography.bodyLarge)
                    if (answered && isCorrectOption) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    } else if (answered && isSelected) {
                        Icon(Icons.Filled.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun FillInBlank(
    question: QuizQuestion,
    lastAnswer: QuizAnswer?,
    onTyped: (String) -> Unit,
) {
    var text by remember(question.id) { mutableStateOf("") }
    val answered = lastAnswer != null

    OutlinedTextField(
        value = text,
        onValueChange = { if (!answered) text = it },
        label = { Text("Jibu lako") },
        singleLine = true,
        enabled = !answered,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(12.dp))
    Button(
        onClick = { onTyped(text) },
        enabled = !answered && text.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Tuma Jibu")
    }
}

@Composable
private fun MatchWords(
    question: QuizQuestion,
    lastAnswer: QuizAnswer?,
    onMatches: (Map<String, String>) -> Unit,
) {
    var selectedLeft by remember(question.id) { mutableStateOf<String?>(null) }
    var pairs by remember(question.id) { mutableStateOf<Map<String, String>>(emptyMap()) }
    val answered = lastAnswer != null

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
                    enabled = !answered && !pairs.containsKey(left.id),
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
                    enabled = !answered && !alreadyUsed && selectedLeft != null,
                    onClick = {
                        selectedLeft?.let { left ->
                            pairs = pairs + (left to right.id)
                            selectedLeft = null
                            if (pairs.size == question.matchLeft.size) onMatches(pairs)
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
private fun FeedbackCard(correct: Boolean, explanation: String) {
    val container = if (correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
    Card(colors = CardDefaults.cardColors(containerColor = container)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (correct) Icons.Filled.CheckCircle else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    if (correct) "Sahihi!" else "Sio Sahihi",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
                Text(explanation, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ResultState(result: QuizResult, onDone: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            if (result.isPerfect) "🎉 Umepata Alama Kamili!" else "Umemaliza Jaribio!",
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
                Text(
                    "+${result.xpEarned} XP",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text("Sawa")
        }
    }
}
