package com.swahilib.feature.advsearch.view

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.SearchFieldRow
import com.swahilib.feature.home.components.SectionHeader
import java.util.Locale

@Composable
fun AdvSearchScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("YOTE") }

    val types = listOf("YOTE", "MANENO", "NAHAU", "METHALI", "Misemo")

    val words by viewModel.filteredWords.collectAsState(initial = emptyList())
    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = results?.firstOrNull() ?: ""
            query = text
            viewModel.filterData(text)
        }
    }

    fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sema unachotafuta ...")
        }
        speechLauncher.launch(intent)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tafuta Kamusi kwa Kina",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchFieldRow(
                query = query,
                placeholder = "Utafutaji wa kina ...",
                onQueryChange = {
                    query = it
                    viewModel.filterData(it)
                },
                onClear = {
                    query = ""
                    viewModel.filterData("")
                },
                onVoiceSearch = { startVoiceSearch() }
            )

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(types) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) },
                        leadingIcon = if (selectedType == type) {
                            { Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selectedType == "YOTE" || selectedType == "MANENO") {
                    stickyHeader { SectionHeader("Matokeo ya maneno", words.size) }
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
                    }
                }
                if (selectedType == "YOTE" || selectedType == "NAHAU") {
                    stickyHeader { SectionHeader("Matokeo ya nahau", idioms.size) }
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
                    }
                }
                if (selectedType == "YOTE" || selectedType == "METHALI") {
                    stickyHeader { SectionHeader("Matokeo ya methali", proverbs.size) }
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
                    }
                }
                if (selectedType == "YOTE" || selectedType == "MISEMO") {
                    stickyHeader { SectionHeader("Matokeo ya misemo", sayings.size) }
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
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
