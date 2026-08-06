package com.swahilib.feature.history.view

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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.action.ReviewNudgeRow
import com.swahilib.core.ui.components.general.ConfirmDialog
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.feature.history.viewmodel.HistoryViewModel

private enum class HistoryChip(val label: String) {
    USOMAJI("USOMAJI"),
    UTAFUTAJI("UTAFUTAJI"),
}

/** Hands a tapped history/search/review row back to Home's Search tab via the standard
 * NavController savedStateHandle bridge (Home reads "pendingSearchQuery" and pre-fills Search). */
private fun requestSearchOnHome(navController: NavHostController, query: String) {
    navController.previousBackStackEntry?.savedStateHandle?.set("pendingSearchQuery", query)
    navController.popBackStack()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val resolvedHistory by viewModel.resolvedHistory.collectAsState()
    val searches by viewModel.searchHistory.collectAsState()
    val reviewSuggestions by viewModel.reviewSuggestions.collectAsState()

    var selectedChip by rememberSaveable { mutableStateOf(HistoryChip.USOMAJI) }
    var showClearDialog by rememberSaveable { mutableStateOf(false) }

    val hasContent = resolvedHistory.isNotEmpty() || searches.isNotEmpty()
    val hasCurrentTabContent = when (selectedChip) {
        HistoryChip.USOMAJI -> resolvedHistory.isNotEmpty()
        HistoryChip.UTAFUTAJI -> searches.isNotEmpty()
    }

    if (showClearDialog) {
        val (title, message) = when (selectedChip) {
            HistoryChip.USOMAJI -> "Futa Historia ya Usomaji?" to
                "Utaondoa maneno, nahau, misemo na methali ulizoangalia hivi karibuni."
            HistoryChip.UTAFUTAJI -> "Futa Historia ya Utafutaji?" to
                "Utaondoa maneno uliyotafuta hivi karibuni."
        }
        ConfirmDialog(
            title = title,
            message = message,
            onConfirm = {
                when (selectedChip) {
                    HistoryChip.USOMAJI -> viewModel.clearReadingHistory()
                    HistoryChip.UTAFUTAJI -> viewModel.clearSearchHistory()
                }
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false },
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Historia",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    if (hasCurrentTabContent) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Futa Historia")
                        }
                    }
                },
            )
        },
    ) { padding ->
        if (!hasContent) {
            androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
                EmptyState(
                    title = "Hamna Historia",
                    message = "Anza kutazama maneno, nahau, misemo, methali au pia kutafuta.",
                    messageIcon = Icons.Default.History,
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (reviewSuggestions.isNotEmpty()) {
                item(key = "review_nudge") {
                    ReviewNudgeRow(
                        suggestions = reviewSuggestions,
                        onItemClick = { requestSearchOnHome(navController, it.title) },
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
                    rows = resolvedHistory,
                    viewModel = viewModel,
                    navController = navController,
                )

                HistoryChip.UTAFUTAJI -> searchesSection(
                    searches = searches,
                    onItemClick = { requestSearchOnHome(navController, it.title) },
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
