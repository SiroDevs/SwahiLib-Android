package com.swahilib.feature.home.view.screen.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.ui.components.action.ReviewNudgeRow
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.feature.home.view.components.readingsSection
import com.swahilib.feature.home.view.components.searchesSection
import com.swahilib.feature.home.viewmodel.HomeViewModel

private enum class HistoryChip(val label: String, val subTab: HomeViewModel.HistorySubTab) {
    USOMAJI("USOMAJI", HomeViewModel.HistorySubTab.USOMAJI),
    UTAFUTAJI("UTAFUTAJI", HomeViewModel.HistorySubTab.UTAFUTAJI),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeHistory(
    viewModel: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val history by viewModel.history.collectAsState(initial = emptyList())
    val searches by viewModel.searchHistory.collectAsState(initial = emptyList())
    val reviewSuggestions by viewModel.reviewSuggestions.collectAsState(initial = emptyList())

    var selectedChip by rememberSaveable { mutableStateOf(HistoryChip.USOMAJI) }

    LaunchedEffect(Unit) {
        viewModel.refreshHistory()
        viewModel.refreshSearchHistory()
    }

    LaunchedEffect(selectedChip) {
        viewModel.setHistorySubTab(selectedChip.subTab)
    }

    if (history.isEmpty() && searches.isEmpty()) {
        EmptyState(
            title = "Hamna Historia",
            message = "Anza kutazama maneno, nahau, misemo, methali au pia kutafuta.",
            messageIcon = Icons.Default.History,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (reviewSuggestions.isNotEmpty()) {
            item(key = "review_nudge") {
                ReviewNudgeRow(
                    suggestions = reviewSuggestions,
                    onItemClick = { viewModel.requestSearch(it.title) },
                )
            }
        }

        stickyHeader {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                items(HistoryChip.entries) { chip ->
                    FilterChip(
                        selected = selectedChip == chip,
                        onClick = { selectedChip = chip },
                        label = { Text(chip.label) },
                    )
                }
            }
        }

        when (selectedChip) {
            HistoryChip.USOMAJI -> readingsSection(
                history = history,
                viewModel = viewModel,
                navController = navController,
            )

            HistoryChip.UTAFUTAJI -> searchesSection(
                searches = searches,
                onItemClick = { viewModel.requestSearch(it.title) },
            )
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
