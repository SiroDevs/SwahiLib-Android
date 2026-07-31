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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.progress.view.components.StatTile
import com.swahilib.feature.progress.view.components.WeeklyActivityChart
import com.swahilib.feature.progress.viewmodel.ProgressViewModel

@Composable
fun StatisticsScreen(
    navController: NavHostController,
    viewModel: ProgressViewModel,
) {
    LaunchedEffect(Unit) { viewModel.refresh() }
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Takwimu Zangu",
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
            val minutes = (stats?.totalLearningSeconds ?: 0L) / 60
            val accuracyPct = ((stats?.quizAccuracy ?: 0f) * 100).toInt()
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    label = "Dakika za kujifunza",
                    value = minutes.toString(),
                    icon = "⏱️",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "Usahihi wa jaribio",
                    value = "$accuracyPct%",
                    icon = "🎯",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    label = "Michezo iliyocheza",
                    value = stats?.gamesPlayed?.toString() ?: "0",
                    icon = "🎮",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "Maneno mapya",
                    value = stats?.wordsLearned?.toString() ?: "0",
                    icon = "📚",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Shughuli za Wiki",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                "Siku hai wiki hii: ${stats?.activeDaysThisWeek ?: 0}/7",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(Modifier.padding(16.dp)) {
                    WeeklyActivityChart(days = stats?.weeklyActivity.orEmpty())
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
