package com.swahilib.feature.home.view.screen.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.database.model.HistoryEntity
import com.swahilib.core.database.model.SearchEntity
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.home.utils.HistoryGrouper
import com.swahilib.feature.home.utils.HistoryRow
import com.swahilib.feature.home.view.components.ContentItem
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

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.readingsSection(
    history: List<HistoryEntity>,
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    if (history.isEmpty()) {
        item {
            EmptyState(
                title = "Hamna Usomaji",
                message = "Maneno, nahau, misemo na methali unazoangalia zitaonekana hapa",
                messageIcon = Icons.Default.History,
            )
        }
        return
    }

    val rows = HistoryGrouper.group(history) { it.createdAt.toLongOrNull() }

    rows.forEach { row ->
        when (row) {
            is HistoryRow.Header -> {
                stickyHeader(key = "history_header_${row.bucket.label}") {
                    BucketHeader(label = row.bucket.label)
                }
            }

            is HistoryRow.Item -> {
                item(key = "history_${row.data.id}") {
                    val contentItem = viewModel.resolveHistoryItem(row.data)
                    contentItem?.let { item ->
                        when (item) {
                            is ContentItem.Word -> WordItem(
                                word = item.entity,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "word",
                                        item.entity
                                    )
                                    navController.navigate(Routes.WORD)
                                },
                                onLike = { viewModel.likeWord(item.entity) },
                                timestamp = row.timestamp,
                            )

                            is ContentItem.Idiom -> IdiomItem(
                                idiom = item.entity,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "idiom",
                                        item.entity
                                    )
                                    navController.navigate(Routes.IDIOM)
                                },
                                onLike = { viewModel.likeIdiom(item.entity) },
                                timestamp = row.timestamp,
                            )

                            is ContentItem.Proverb -> ProverbItem(
                                proverb = item.entity,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "proverb",
                                        item.entity
                                    )
                                    navController.navigate(Routes.PROVERB)
                                },
                                onLike = { viewModel.likeProverb(item.entity) },
                                timestamp = row.timestamp,
                            )

                            is ContentItem.Saying -> SayingItem(
                                saying = item.entity,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "saying",
                                        item.entity
                                    )
                                    navController.navigate(Routes.SAYING)
                                },
                                onLike = { viewModel.likeSaying(item.entity) },
                                timestamp = row.timestamp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.searchesSection(
    searches: List<SearchEntity>,
    onItemClick: (SearchEntity) -> Unit,
) {
    if (searches.isEmpty()) {
        item {
            EmptyState(
                title = "Hamna Utafutaji",
                message = "Chochote unachotafuta kitaonekana hapa",
                messageIcon = Icons.Default.ManageSearch,
            )
        }
        return
    }

    val rows = HistoryGrouper.group(searches) { it.createdAt.toLongOrNull() }

    rows.forEach { row ->
        when (row) {
            is HistoryRow.Header -> {
                stickyHeader(key = "search_header_${row.bucket.label}") {
                    BucketHeader(label = row.bucket.label)
                }
            }

            is HistoryRow.Item -> {
                item(key = "search_${row.data.id}") {
                    ListItem(
                        headlineContent = { Text(row.data.title) },
                        supportingContent = { Text(row.timestamp) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.clickable { onItemClick(row.data) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BucketHeader(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
