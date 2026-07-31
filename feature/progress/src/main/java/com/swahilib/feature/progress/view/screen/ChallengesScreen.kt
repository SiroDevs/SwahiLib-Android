package com.swahilib.feature.progress.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.swahilib.feature.progress.view.components.ChallengeCard
import com.swahilib.feature.progress.viewmodel.ProgressViewModel

@Composable
fun ChallengesScreen(
    navController: NavHostController,
    viewModel: ProgressViewModel,
) {
    LaunchedEffect(Unit) { viewModel.refresh() }
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
                        onCompleteActivity = { activityId ->
                            viewModel.completeActivity(c.id, activityId)
                        },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
