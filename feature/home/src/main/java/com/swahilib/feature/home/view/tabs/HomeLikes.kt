package com.swahilib.feature.home.view.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.*
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
        if (likedWords.isNotEmpty()) {
            stickyHeader { SectionHeader(title = "Maneno", count = likedWords.size) }
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
        }
        if (likedIdioms.isNotEmpty()) {
            stickyHeader { SectionHeader(title = "Nahau", count = likedIdioms.size) }
            items(likedIdioms, key = { it.rid }) { idiom ->
                IdiomItem(
                    idiom = idiom,
                    onTap = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("idiom", idiom)
                        navController.navigate(Routes.IDIOM)
                    },
                    onLike = { viewModel.likeIdiom(idiom) }
                )
            }
        }
        if (likedProverbs.isNotEmpty()) {
            stickyHeader { SectionHeader(title = "Methali", count = likedProverbs.size) }
            items(likedProverbs, key = { it.rid }) { proverb ->
                ProverbItem(
                    proverb = proverb,
                    onTap = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("proverb", proverb)
                        navController.navigate(Routes.PROVERB)
                    },
                    onLike = { viewModel.likeProverb(proverb) }
                )
            }
        }
        if (likedSayings.isNotEmpty()) {
            stickyHeader { SectionHeader(title = "Misemo", count = likedSayings.size) }
            items(likedSayings, key = { it.rid }) { saying ->
                SayingItem(
                    saying = saying,
                    onTap = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("saying", saying)
                        navController.navigate(Routes.SAYING)
                    },
                    onLike = { viewModel.likeSaying(saying) }
                )
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
