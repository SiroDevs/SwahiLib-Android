package com.swahilib.feature.progress.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.progress.ChallengeCard
import com.swahilib.core.ui.components.progress.RecommendationRow
import com.swahilib.core.ui.components.progress.routeForChallengeActivity
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
    val recommendations by viewModel.recommendations.collectAsState()

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

            if (recommendations.isNotEmpty()) {
                Text(
                    "Kwa Ajili Yako",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(8.dp))
                androidx.compose.foundation.layout.Column(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                ) {
                    recommendations.forEach { rec ->
                        RecommendationRow(rec) { route -> navController.navigate(route) }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            if (challenges.isEmpty()) {
                Text(
                    "Hamna changamoto ya sasa. Jaribu tena baadaye!",
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
        }
    }
}
