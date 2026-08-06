package com.swahilib.feature.home.view.screen.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.general.StreakBadge
import com.swahilib.core.ui.components.progress.AchievementCard
import com.swahilib.core.ui.components.progress.ChallengeCard
import com.swahilib.core.ui.components.progress.RecommendationRow
import com.swahilib.core.ui.components.progress.SectionHeader
import com.swahilib.core.ui.components.progress.StatTile
import com.swahilib.core.ui.components.progress.XpProgressCard
import com.swahilib.core.ui.components.progress.routeForChallengeActivity
import com.swahilib.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeEngagement(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) { viewModel.refreshProgress() }

    // Re-pull whenever Home resumes (e.g. returning from a game after completing an activity) -
    // same pattern as ChallengesScreen, since this data is fetched once rather than observed live.
    val currentOnResume = rememberUpdatedState(viewModel::refreshProgress)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) currentOnResume.value()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val progress by viewModel.progress.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
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

        OutlinedButton(
            onClick = { navController.navigate(Routes.quizFreeplay()) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("🎯 Jaribio la Haraka (Mazoezi)")
        }
        Spacer(Modifier.height(20.dp))

        if (recommendations.isNotEmpty()) {
            SectionHeader(title = "Kwa Ajili Yako")
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                recommendations.forEach { rec ->
                    RecommendationRow(rec) { route -> navController.navigate(route) }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

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
                        val route = routeForChallengeActivity(c, activity)
                        if (route != null) {
                            navController.navigate(route)
                        } else {
                            viewModel.completeActivity(c.id, activity.id)
                        }
                    },
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        SectionHeader(
            title = "Beji",
            actionLabel = "Zote",
            onAction = { navController.navigate(Routes.ACHIEVEMENTS) },
        )
        Spacer(Modifier.height(8.dp))
        if (achievements.isEmpty()) {
            Text(
                "Beji zitaonekana hapa unapoendelea kujifunza.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(
                    achievements.sortedByDescending { it.unlocked }.take(6),
                    key = { it.id },
                ) { achievement ->
                    AchievementCard(achievement, modifier = Modifier.width(130.dp))
                }
            }
        }

        Spacer(Modifier.height(20.dp))
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
        Spacer(Modifier.height(24.dp))
    }
}
