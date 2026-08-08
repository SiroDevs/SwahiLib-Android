package com.swahilib.feature.progress.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.progress.AchievementCard
import com.swahilib.feature.progress.viewmodel.ProgressViewModel

@Composable
fun AchievementsScreen(
    navController: NavHostController,
    viewModel: ProgressViewModel,
) {
    LaunchedEffect(Unit) { viewModel.refresh() }
    val achievements by viewModel.achievements.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Beji",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            items(achievements, key = { it.id }) { AchievementCard(it) }
        }
    }
}
