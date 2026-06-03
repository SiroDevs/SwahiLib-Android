package com.swahilib.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.ui.components.donation.DonationBanner
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.home.HomeViewModel

@Composable
fun HomeSearchResults(
    selectedType: String,
    words: List<WordEntity>,
    idioms: List<IdiomEntity>,
    proverbs: List<ProverbEntity>,
    sayings: List<SayingEntity>,
    showDonation: Boolean,
    listState: LazyListState,
    viewModel: HomeViewModel,
    navController: NavHostController,
    onShowDonationDialog: () -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 70.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (selectedType) {
            "MANENO" -> {
                item { SectionHeader("Matokeo", words.size) }
                if (words.isNotEmpty()) {
                    item { SectionHeader("Matokeo", words.size) }
                    if (words.isNotEmpty()) {
                        itemsIndexed(words, key = { _, w -> w.rid }) { index, word ->
                            if (index == 4 || index == 7) DonationBanner(
                                show = showDonation,
                                onTap = onShowDonationDialog
                            )
                            WordItem(
                                word = word,
                                onTap = {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "word",
                                        word
                                    )
                                    viewModel.addToHistory(word.rid, "word")
                                    navController.navigate(Routes.WORD)
                                },
                                onLike = { viewModel.likeWord(word) }
                            )
                        }
                    }
                }
            }

            "NAHAU" -> {
                item { SectionHeader("Matokeo", idioms.size) }
                if (idioms.isNotEmpty()) {
                    itemsIndexed(idioms, key = { _, i -> i.rid }) { index, idiom ->
                        if (index == 3 || index == 7) DonationBanner(
                            show = showDonation,
                            onTap = onShowDonationDialog
                        )
                        IdiomItem(
                            idiom = idiom,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "idiom",
                                    idiom
                                )
                                viewModel.addToHistory(idiom.rid, "idiom")
                                navController.navigate(Routes.IDIOM)
                            },
                            onLike = { viewModel.likeIdiom(idiom) }
                        )
                    }
                }
            }

            "METHALI" -> {
                item { SectionHeader("Matokeo", proverbs.size) }
                if (proverbs.isNotEmpty()) {
                    itemsIndexed(proverbs, key = { _, p -> p.rid }) { index, proverb ->
                        if (index == 4 || index == 10) DonationBanner(
                            show = showDonation,
                            onTap = onShowDonationDialog
                        )
                        ProverbItem(
                            proverb = proverb,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "proverb",
                                    proverb
                                )
                                viewModel.addToHistory(proverb.rid, "proverb")
                                navController.navigate(Routes.PROVERB)
                            },
                            onLike = { viewModel.likeProverb(proverb) }
                        )
                    }
                }
            }

            "MISEMO" -> {
                item { SectionHeader("Matokeo", sayings.size) }
                if (sayings.isNotEmpty()) {
                    itemsIndexed(sayings, key = { _, s -> s.rid }) { index, saying ->
                        if (index == 2 || index == 6) DonationBanner(
                            show = showDonation,
                            onTap = onShowDonationDialog
                        )
                        SayingItem(
                            saying = saying,
                            onTap = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "saying",
                                    saying
                                )
                                viewModel.addToHistory(saying.rid, "saying")
                                navController.navigate(Routes.SAYING)
                            },
                            onLike = { viewModel.likeSaying(saying) }
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}