package com.swahilib.feature.home.view.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.ContentItem
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun HomeHistory(
    viewModel: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val history by viewModel.history.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) { viewModel.refreshHistory() }

    if (history.isEmpty()) {
        EmptyState(
            title = "Hamna Historia",
            message = "Maneno, nahau, misemo na methali unazoangalia zitaonekana hapa",
            messageIcon = Icons.Default.History
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(history, key = { it.id }) { historyEntry ->
            val contentItem = viewModel.resolveHistoryItem(historyEntry)
            val ms = historyEntry.createdAt.toLong()
            val sdf = SimpleDateFormat("dd MMM, HH:mm", LocalLocale.current.platformLocale)
            val timestamp = try {
                sdf.format(Date(ms))
            } catch (e: Exception) { historyEntry.createdAt }

            contentItem?.let { item ->
                Column {
                    when (item) {
                        is ContentItem.Word -> WordItem(
                            word = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("word", item.entity)
                                navController.navigate(Routes.WORD)
                            },
                            onLike = { viewModel.likeWord(item.entity) },
                            timestamp = timestamp
                        )
                        is ContentItem.Idiom -> IdiomItem(
                            idiom = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("idiom", item.entity)
                                navController.navigate(Routes.IDIOM)
                            },
                            onLike = { viewModel.likeIdiom(item.entity) },
                            timestamp = timestamp
                        )
                        is ContentItem.Proverb -> ProverbItem(
                            proverb = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("proverb", item.entity)
                                navController.navigate(Routes.PROVERB)
                            },
                            onLike = { viewModel.likeProverb(item.entity) },
                            timestamp = timestamp
                        )
                        is ContentItem.Saying -> SayingItem(
                            saying = item.entity,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("saying", item.entity)
                                navController.navigate(Routes.SAYING)
                            },
                            onLike = { viewModel.likeSaying(item.entity) },
                            timestamp = timestamp
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}