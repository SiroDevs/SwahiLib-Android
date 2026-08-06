package com.swahilib.feature.history.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.database.model.SearchEntity
import com.swahilib.core.ui.components.action.BucketHeader
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.history.model.ContentItem
import com.swahilib.feature.history.utils.HistoryGrouper
import com.swahilib.feature.history.utils.HistoryRow
import com.swahilib.feature.history.viewmodel.HistoryViewModel

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.readingsSection(
    rows: List<HistoryViewModel.ResolvedHistoryItem>,
    viewModel: HistoryViewModel,
    navController: NavHostController,
) {
    if (rows.isEmpty()) {
        item {
            EmptyState(
                title = "Hamna Usomaji",
                message = "Maneno, nahau, misemo na methali unazoangalia zitaonekana hapa",
                messageIcon = Icons.Default.History,
            )
        }
        return
    }

    val grouped = HistoryGrouper.group(rows) { it.history.createdAt.toLongOrNull() }

    grouped.forEach { row ->
        when (row) {
            is HistoryRow.Header -> {
                stickyHeader(key = "history_header_${row.bucket.label}") {
                    BucketHeader(label = row.bucket.label)
                }
            }

            is HistoryRow.Item -> {
                item(key = "history_${row.data.history.id}") {
                    when (val item = row.data.content) {
                        is ContentItem.Word -> WordItem(
                            word = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("word", item.entity)
                                navController.navigate(Routes.WORD)
                            },
                            onLike = { viewModel.likeWord(item.entity) },
                            timestamp = row.timestamp,
                        )

                        is ContentItem.Idiom -> IdiomItem(
                            idiom = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("idiom", item.entity)
                                navController.navigate(Routes.IDIOM)
                            },
                            onLike = { viewModel.likeIdiom(item.entity) },
                            timestamp = row.timestamp,
                        )

                        is ContentItem.Proverb -> ProverbItem(
                            proverb = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("proverb", item.entity)
                                navController.navigate(Routes.PROVERB)
                            },
                            onLike = { viewModel.likeProverb(item.entity) },
                            timestamp = row.timestamp,
                        )

                        is ContentItem.Saying -> SayingItem(
                            saying = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("saying", item.entity)
                                navController.navigate(Routes.SAYING)
                            },
                            onLike = { viewModel.likeSaying(item.entity) },
                            timestamp = row.timestamp,
                        )

                        null -> { /* content no longer resolvable (e.g. deleted) - skip */ }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.searchesSection(
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
