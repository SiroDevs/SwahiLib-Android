package com.swahilib.feature.home.view.tabs

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.*
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.VerticalLetters
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeSearch(
    viewModel: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedLetter by rememberSaveable { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())
    val words by viewModel.filteredWords.collectAsState(initial = emptyList())

    // Voice search launcher
    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = results?.firstOrNull() ?: ""
            searchQuery = text
            selectedLetter = ""
            viewModel.filterData(text)
        }
    }

    fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sema neno la kutafuta...")
        }
        speechLauncher.launch(intent)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 78.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                // Modern Search Box
                SearchFieldRow(
                    query = searchQuery,
                    placeholder = "Tafuta Kamusi...",
                    onQueryChange = {
                        searchQuery = it
                        selectedLetter = ""
                        viewModel.filterData(it)
                    },
                    onClear = {
                        searchQuery = ""
                        viewModel.filterData("")
                    },
                    onVoiceSearch = { startVoiceSearch() }
                )
            }

            // Words section
            if (words.isNotEmpty()) {
                stickyHeader {
                    SectionHeader(title = "Maneno", count = words.size)
                }
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

            // Idioms section
            if (idioms.isNotEmpty()) {
                stickyHeader {
                    SectionHeader(title = "Nahau", count = idioms.size)
                }
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

            // Proverbs section
            if (proverbs.isNotEmpty()) {
                stickyHeader {
                    SectionHeader(title = "Methali", count = proverbs.size)
                }
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

            // Sayings section
            if (sayings.isNotEmpty()) {
                stickyHeader {
                    SectionHeader(title = "Misemo", count = sayings.size)
                }
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

            if (words.isEmpty() && idioms.isEmpty() && proverbs.isEmpty() && sayings.isEmpty()) {
                item { EmptyState(message = "Hamna matokeo ya utafutaji") }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        // Vertical letter scrubber
        VerticalLetters(
            selectedLetter = selectedLetter,
            onLetterSelected = { letter ->
                selectedLetter = letter
                searchQuery = letter
                viewModel.filterData(letter)
            },
        )

        // FAB for Advanced Search
        FloatingActionButton(
            onClick = { navController.navigate(Routes.ADVSEARCH) },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 16.dp)
                .size(52.dp)
        ) {
            Icon(
                Icons.Filled.ManageSearch,
                contentDescription = "Tafuta kwa Kina",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun SearchFieldRow(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onVoiceSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            Row {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Filled.Clear, contentDescription = "Futa")
                    }
                }
                IconButton(onClick = onVoiceSearch) {
                    Icon(Icons.Filled.Mic, contentDescription = "Tafuta kwa Sauti")
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary,
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
