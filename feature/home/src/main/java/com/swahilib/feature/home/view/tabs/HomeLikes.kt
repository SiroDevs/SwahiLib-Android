package com.swahilib.feature.home.view.tabs

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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.SectionHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeLikes(
    viewModel: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val likedWords by viewModel.likedWords.collectAsState(initial = emptyList())
    val likedIdioms by viewModel.likedIdioms.collectAsState(initial = emptyList())
    val likedProverbs by viewModel.likedProverbs.collectAsState(initial = emptyList())
    val likedSayings by viewModel.likedSayings.collectAsState(initial = emptyList())

    var selectedType by rememberSaveable { mutableStateOf("MANENO") }
    val types = listOf("MANENO", "NAHAU", "METHALI", "Misemo")

    val hasLikes = likedWords.isNotEmpty() || likedIdioms.isNotEmpty() ||
        likedProverbs.isNotEmpty() || likedSayings.isNotEmpty()

    if (!hasLikes) {
        EmptyState(
            title = "Hamna Vipendwa",
            message = "Bonyeza moyo kwenye neno, nahau, msemo au methali unayopenda ili kuongeza hapa",
            messageIcon = Icons.Default.FavoriteBorder
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        stickyHeader {
            LazyRow(
                modifier = Modifier.padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(types) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) },
                    )
                }
            }
        }

        if (selectedType == "MANENO") {
            if (likedWords.isNotEmpty()) {
                stickyHeader { SectionHeader(title = "Maneno uliyopenda", count = likedWords.size) }
                items(likedWords, key = { it.rid }) { word ->
                    WordItem(
                        word = word,
                        onTap = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("word", word)
                            navController.navigate(Routes.WORD)
                        },
                        onLike = { viewModel.likeWord(word) }
                    )
                }
            } else {
                item {
                    EmptyState(
                        title = "Hamna Vipendwa vya Maneno",
                        message = "Bonyeza moyo kwenye neno unalopenda ili kuongeza hapa",
                        messageIcon = Icons.Default.FavoriteBorder
                    )
                }
            }
        }

        if (selectedType == "IDIOMS") {
            if (likedIdioms.isNotEmpty()) {
                stickyHeader { SectionHeader(title = "Nahau ulizopenda", count = likedIdioms.size) }
                items(likedIdioms, key = { it.rid }) { idiom ->
                    IdiomItem(
                        idiom = idiom,
                        onTap = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "idiom",
                                idiom
                            )
                            navController.navigate(Routes.IDIOM)
                        },
                        onLike = { viewModel.likeIdiom(idiom) }
                    )
                }
            } else {
                item {
                    EmptyState(
                        title = "Hamna Vipendwa vya Nahau",
                        message = "Bonyeza moyo kwenye nahau unayopenda ili kuongeza hapa",
                        messageIcon = Icons.Default.FavoriteBorder
                    )
                }
            }
        }

        if (selectedType == "PROVERBS") {
            if (likedProverbs.isNotEmpty()) {
                stickyHeader {
                    SectionHeader(
                        title = "Methali ulizopenda",
                        count = likedProverbs.size
                    )
                }
                items(likedProverbs, key = { it.rid }) { proverb ->
                    ProverbItem(
                        proverb = proverb,
                        onTap = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "proverb",
                                proverb
                            )
                            navController.navigate(Routes.PROVERB)
                        },
                        onLike = { viewModel.likeProverb(proverb) }
                    )
                }
            } else {
                item {
                    EmptyState(
                        title = "Hamna Vipendwa vya Methali",
                        message = "Bonyeza moyo kwenye methali unayopenda ili kuongeza hapa",
                        messageIcon = Icons.Default.FavoriteBorder
                    )
                }
            }
        }

        if (selectedType == "SAYINGS") {
            if (likedSayings.isNotEmpty()) {
                stickyHeader {
                    SectionHeader(
                        title = "Misemo uliyopenda",
                        count = likedSayings.size
                    )
                }
                items(likedSayings, key = { it.rid }) { saying ->
                    SayingItem(
                        saying = saying,
                        onTap = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "saying",
                                saying
                            )
                            navController.navigate(Routes.SAYING)
                        },
                        onLike = { viewModel.likeSaying(saying) }
                    )
                }
            } else {
                item {
                    EmptyState(
                        title = "Hamna Vipendwa vya Misemo",
                        message = "Bonyeza moyo kwenye msemo unaopenda ili kuongeza hapa",
                        messageIcon = Icons.Default.FavoriteBorder
                    )
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
