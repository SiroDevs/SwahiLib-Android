package com.swahilib.feature.progress.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.StreakBadge
import com.swahilib.feature.progress.view.components.ChallengeCard
import com.swahilib.feature.progress.view.components.StatTile
import com.swahilib.feature.progress.view.components.XpProgressCard
import com.swahilib.feature.progress.viewmodel.ProgressViewModel

@Composable
fun ProgressScreen(
    navController: NavHostController,
    viewModel: ProgressViewModel,
) {
    LaunchedEffect(Unit) { viewModel.refresh() }

    val progress by viewModel.progress.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Maendeleo Yangu",
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
            progress?.let {
                XpProgressCard(it)
                Spacer(Modifier.height(12.dp))
                StreakBadge(streakCount = it.currentStreak)
                if (it.currentStreak > 1) Spacer(Modifier.height(12.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    label = "Ngazi",
                    value = progress?.level?.toString() ?: "1",
                    icon = "🎖️",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "Mfuatano bora",
                    value = progress?.bestStreak?.toString() ?: "0",
                    icon = "🔥",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    label = "Changamoto",
                    value = progress?.challengesCompleted?.toString() ?: "0",
                    icon = "🎯",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "Shughuli",
                    value = progress?.activitiesCompleted?.toString() ?: "0",
                    icon = "✅",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))

            SectionHeader(
                title = "Changamoto Zinazoendelea",
                actionLabel = "Zote",
                onAction = { navController.navigate(Routes.CHALLENGES) },
            )
            Spacer(Modifier.height(8.dp))
            if (challenges.isEmpty()) {
                Text(
                    "Hakuna changamoto ya sasa. Rudi kesho!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                challenges.forEach { c ->
                    ChallengeCard(
                        challenge = c,
                        onStartActivity = { activity ->
                            when (activity.type) {
                                ActivityType.VOCABULARY_QUIZ ->
                                    navController.navigate(Routes.quiz(c.id, activity.id, c.difficulty.name))
                                ActivityType.PROVERB_CHALLENGE ->
                                    navController.navigate(Routes.quiz(c.id, activity.id, c.difficulty.name, source = "PROVERBS"))
                                ActivityType.WORD_BUILDER ->
                                    navController.navigate(Routes.wordBuilder(c.id, activity.id, c.difficulty.name))
                                ActivityType.SENTENCE_BUILDER ->
                                    navController.navigate(Routes.sentenceBuilder(c.id, activity.id, c.difficulty.name))
                                ActivityType.SPELLING_CHALLENGE ->
                                    navController.navigate(Routes.spelling(c.id, activity.id, c.difficulty.name))
                                ActivityType.CROSSWORD ->
                                    navController.navigate(Routes.crossword(c.id, activity.id, c.difficulty.name))
                                ActivityType.WORD_SEARCH ->
                                    navController.navigate(Routes.wordSearch(c.id, activity.id, c.difficulty.name))
                                else -> viewModel.completeActivity(c.id, activity.id)
                            }
                        },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
            SectionHeader(
                title = "Takwimu",
                actionLabel = "Zaidi",
                onAction = { navController.navigate(Routes.STATISTICS) },
            )
            Spacer(Modifier.height(8.dp))
            val minutes = (stats?.totalLearningSeconds ?: 0L) / 60
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    label = "Maneno",
                    value = stats?.wordsLearned?.toString() ?: "0",
                    icon = "📚",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "Dakika",
                    value = minutes.toString(),
                    icon = "⏱️",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(20.dp))
            SectionHeader(
                title = "Beji",
                actionLabel = "Zote",
                onAction = { navController.navigate(Routes.ACHIEVEMENTS) },
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )
        if (actionLabel != null) TextButton(onClick = onAction) { Text(actionLabel) }
    }
}
