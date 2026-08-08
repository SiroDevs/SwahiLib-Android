package com.swahilib.feature.likes.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.ConfirmDialog
import com.swahilib.core.ui.components.general.SectionCountHeader
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.likes.viewmodel.LikesViewModel

private enum class LikesTab(val label: String) {
    MANENO("MANENO"),
    NAHAU("NAHAU"),
    METHALI("METHALI"),
    MISEMO("MISEMO"),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LikesScreen(
    navController: NavHostController,
    viewModel: LikesViewModel = hiltViewModel(),
) {
    val likedWords by viewModel.likedWords.collectAsState()
    val likedIdioms by viewModel.likedIdioms.collectAsState()
    val likedProverbs by viewModel.likedProverbs.collectAsState()
    val likedSayings by viewModel.likedSayings.collectAsState()

    var selectedTab by rememberSaveable { mutableStateOf(LikesTab.MANENO) }
    var showClearDialog by rememberSaveable { mutableStateOf(false) }

    val hasLikes = likedWords.isNotEmpty() || likedIdioms.isNotEmpty() ||
        likedProverbs.isNotEmpty() || likedSayings.isNotEmpty()

    if (showClearDialog) {
        ConfirmDialog(
            title = "Futa Vipendwa Vyote?",
            message = "Utaondoa moyo kwenye vitu vyote ulivyopenda. Hatua hii haiwezi kutenduliwa.",
            onConfirm = {
                viewModel.clearAllLikes()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false },
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Vipendwa",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    if (hasLikes) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Futa Vipendwa Vyote")
                        }
                    }
                },
            )
        },
    ) { padding ->
        if (!hasLikes) {
            androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
                EmptyState(
                    title = "Hamna Vipendwa",
                    message = "Bonyeza moyo kwenye neno, nahau, msemo au methali unayopenda ili kuongeza hapa",
                    messageIcon = Icons.Default.FavoriteBorder,
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
            stickyHeader {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    items(LikesTab.entries) { tab ->
                        FilterChip(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }

            when (selectedTab) {
                LikesTab.MANENO -> {
                    if (likedWords.isNotEmpty()) {
                        stickyHeader { SectionCountHeader("Maneno uliyopenda", likedWords.size) }
                        items(likedWords, key = { it.rid }) { word ->
                            WordItem(
                                word = word,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("word", word)
                                    navController.navigate(Routes.WORD)
                                },
                                onLike = { viewModel.likeWord(word) },
                            )
                        }
                    } else {
                        item { EmptyState("Hamna Vipendwa vya Maneno", messageIcon = Icons.Default.FavoriteBorder) }
                    }
                }
                LikesTab.NAHAU -> {
                    if (likedIdioms.isNotEmpty()) {
                        stickyHeader { SectionCountHeader("Nahau ulizopenda", likedIdioms.size) }
                        items(likedIdioms, key = { it.rid }) { idiom ->
                            IdiomItem(
                                idiom = idiom,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("idiom", idiom)
                                    navController.navigate(Routes.IDIOM)
                                },
                                onLike = { viewModel.likeIdiom(idiom) },
                            )
                        }
                    } else {
                        item { EmptyState("Hamna Vipendwa vya Nahau", messageIcon = Icons.Default.FavoriteBorder) }
                    }
                }
                LikesTab.METHALI -> {
                    if (likedProverbs.isNotEmpty()) {
                        stickyHeader { SectionCountHeader("Methali ulizopenda", likedProverbs.size) }
                        items(likedProverbs, key = { it.rid }) { proverb ->
                            ProverbItem(
                                proverb = proverb,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("proverb", proverb)
                                    navController.navigate(Routes.PROVERB)
                                },
                                onLike = { viewModel.likeProverb(proverb) },
                            )
                        }
                    } else {
                        item { EmptyState("Hamna Vipendwa vya Methali", messageIcon = Icons.Default.FavoriteBorder) }
                    }
                }
                LikesTab.MISEMO -> {
                    if (likedSayings.isNotEmpty()) {
                        stickyHeader { SectionCountHeader("Misemo uliyopenda", likedSayings.size) }
                        items(likedSayings, key = { it.rid }) { saying ->
                            SayingItem(
                                saying = saying,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set("saying", saying)
                                    navController.navigate(Routes.SAYING)
                                },
                                onLike = { viewModel.likeSaying(saying) },
                            )
                        }
                    } else {
                        item { EmptyState("Hamna Vipendwa vya Misemo", messageIcon = Icons.Default.FavoriteBorder) }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
