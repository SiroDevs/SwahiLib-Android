package com.swahilib.feature.advsearch.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.advsearch.AdvSearchViewModel

@Composable
fun NoResultsPlaceholder(query: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Search, null, Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Hamna matokeo kwa \"$query\"",
                style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Jaribu maneno tofauti au tazama tahajia",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline, fontSize = 12.sp
            )
        }
    }
}

fun LazyListScope.WordsSection(
    words: List<WordEntity>, query: String, show: Boolean,
    navController: NavHostController, viewModel: AdvSearchViewModel,
) {
    if (!show) return
    if (words.isNotEmpty()) {
        items(words, key = { it.rid }) { word ->
            WordItem(
                word = word,
                onTap = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("word", word)
                    viewModel.addToHistory(word.rid, "word")
                    navController.navigate(Routes.WORD)
                },
                onLike = { viewModel.likeWord(word) }
            )
        }
    } else if (query.isNotEmpty()) {
        item { EmptySection("maneno") }
    }
}

fun LazyListScope.IdiomsSection(
    idioms: List<IdiomEntity>, query: String, show: Boolean,
    navController: NavHostController, viewModel: AdvSearchViewModel,
) {
    if (!show) return
    if (idioms.isNotEmpty()) {
        items(idioms, key = { it.rid }) { idiom ->
            IdiomItem(
                idiom = idiom,
                onTap = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("idiom", idiom)
                    viewModel.addToHistory(idiom.rid, "idiom")
                    navController.navigate(Routes.IDIOM)
                },
                onLike = { viewModel.likeIdiom(idiom) }
            )
        }
    } else if (query.isNotEmpty()) {
        item { EmptySection("nahau") }
    }
}

fun LazyListScope.ProverbsSection(
    proverbs: List<ProverbEntity>, query: String, show: Boolean,
    navController: NavHostController, viewModel: AdvSearchViewModel,
) {
    if (!show) return
    if (proverbs.isNotEmpty()) {
        items(proverbs, key = { it.rid }) { proverb ->
            ProverbItem(
                proverb = proverb,
                onTap = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("proverb", proverb)
                    viewModel.addToHistory(proverb.rid, "proverb")
                    navController.navigate(Routes.PROVERB)
                },
                onLike = { viewModel.likeProverb(proverb) }
            )
        }
    } else if (query.isNotEmpty()) {
        item { EmptySection("methali") }
    }
}

fun LazyListScope.SayingsSection(
    sayings: List<SayingEntity>, query: String, show: Boolean,
    navController: NavHostController, viewModel: AdvSearchViewModel,
) {
    if (!show) return
    if (sayings.isNotEmpty()) {
        items(sayings, key = { it.rid }) { saying ->
            SayingItem(
                saying = saying,
                onTap = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("saying", saying)
                    viewModel.addToHistory(saying.rid, "saying")
                    navController.navigate(Routes.SAYING)
                },
                onLike = { viewModel.likeSaying(saying) }
            )
        }
    } else if (query.isNotEmpty()) {
        item { EmptySection("misemo") }
    }
}