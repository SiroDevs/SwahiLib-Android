package com.swahilib.feature.progress.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.progress.view.components.ChallengeCard
import com.swahilib.feature.progress.viewmodel.ProgressViewModel

@Composable
fun ChallengesScreen(
    navController: NavHostController,
    viewModel: ProgressViewModel,
) {
    LaunchedEffect(Unit) { viewModel.refresh() }

    // Re-pull challenge state whenever this screen resumes (e.g. returning
    // from the Quiz screen after completing an activity).
    val currentOnResume = rememberUpdatedState(viewModel::refresh)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) currentOnResume.value()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val challenges by viewModel.challenges.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Changamoto",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            OutlinedButton(
                onClick = { navController.navigate(Routes.quizFreeplay()) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("🎯 Jaribio la Haraka (Mazoezi)")
            }
            Spacer(Modifier.height(16.dp))

            if (challenges.isEmpty()) {
                Text(
                    "Hakuna changamoto ya sasa. Fungua tena baadaye!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    "Changamoto Zinazoendelea",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(12.dp))
                challenges.forEach { c ->
                    ChallengeCard(
                        challenge = c,
                        onStartActivity = { activity ->
                            when (activity.type) {
                                ActivityType.VOCABULARY_QUIZ -> navController.navigate(
                                    Routes.quiz(c.id, activity.id, c.difficulty.name)
                                )
                                ActivityType.PROVERB_CHALLENGE -> navController.navigate(
                                    Routes.quiz(c.id, activity.id, c.difficulty.name, source = "PROVERBS")
                                )
                                ActivityType.WORD_BUILDER -> navController.navigate(
                                    Routes.wordBuilder(c.id, activity.id, c.difficulty.name)
                                )
                                ActivityType.SENTENCE_BUILDER -> navController.navigate(
                                    Routes.sentenceBuilder(c.id, activity.id, c.difficulty.name)
                                )
                                ActivityType.SPELLING_CHALLENGE -> navController.navigate(
                                    Routes.spelling(c.id, activity.id, c.difficulty.name)
                                )
                                ActivityType.CROSSWORD -> navController.navigate(
                                    Routes.crossword(c.id, activity.id, c.difficulty.name)
                                )
                                ActivityType.WORD_SEARCH -> navController.navigate(
                                    Routes.wordSearch(c.id, activity.id, c.difficulty.name)
                                )
                                else -> viewModel.completeActivity(c.id, activity.id)
                            }
                        },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
