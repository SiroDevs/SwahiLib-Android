package com.swahilib.feature.advsearch.view

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.listitems.*
import com.swahilib.feature.home.HomeViewModel
import java.util.Locale

@Composable
fun AdvSearchScreen(
    navController: NavHostController,
    viewModel: HomeViewModel,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("Yote") }

    val types = listOf("Yote", "Maneno", "Nahau", "Methali", "Misemo")

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

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tafuta kwa Kina",
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
            // Search field
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; viewModel.filterData(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Andika neno la kutafuta...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    Row {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = ""; viewModel.filterData("") }) {
                                Icon(Icons.Filled.Clear, null)
                            }
                        }
                        IconButton(onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Sema neno la kutafuta...")
                            }
                            speechLauncher.launch(intent)
                        }) {
                            Icon(Icons.Filled.Mic, null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Type filter chips
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

            // Results
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selectedType == "Yote" || selectedType == "Maneno") {
                    if (words.isNotEmpty()) {
                        stickyHeader { com.swahilib.feature.home.view.tabs.SectionHeader("Maneno", words.size) }
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
                if (selectedType == "Yote" || selectedType == "Nahau") {
                    if (idioms.isNotEmpty()) {
                        stickyHeader { com.swahilib.feature.home.view.tabs.SectionHeader("Nahau", idioms.size) }
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
                if (selectedType == "Yote" || selectedType == "Methali") {
                    if (proverbs.isNotEmpty()) {
                        stickyHeader { com.swahilib.feature.home.view.tabs.SectionHeader("Methali", proverbs.size) }
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
                if (selectedType == "Yote" || selectedType == "Misemo") {
                    if (sayings.isNotEmpty()) {
                        stickyHeader { com.swahilib.feature.home.view.tabs.SectionHeader("Misemo", sayings.size) }
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
