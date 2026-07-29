/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.feature.home.view.components

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
import com.swahilib.core.database.model.HistoryEntity
import com.swahilib.core.database.model.SearchEntity
import com.swahilib.core.ui.components.action.BucketHeader
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.home.utils.HistoryGrouper
import com.swahilib.feature.home.utils.HistoryRow
import com.swahilib.feature.home.viewmodel.HomeViewModel

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.readingsSection(
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
