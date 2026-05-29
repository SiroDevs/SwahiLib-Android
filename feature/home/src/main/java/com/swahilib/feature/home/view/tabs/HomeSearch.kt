package com.swahilib.feature.home.view.tabs

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.swahilib.core.ui.components.listitems.*
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.SearchFieldRow
import com.swahilib.feature.home.components.SectionHeader
import com.swahilib.feature.home.components.VerticalLetters
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeSearch(
    viewModel: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedLetter by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("MANENO") }
    val types = listOf("MANENO", "NAHAU", "METHALI", "MISEMO")

    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())
    val words by viewModel.filteredWords.collectAsState(initial = emptyList())

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // FAB is extended when at the very top, collapsed when scrolled down
    val isAtTop by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    // Scroll-to-top button appears as soon as we leave the top
    val showScrollToTop by remember { derivedStateOf { !isAtTop } }

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: ""
            searchQuery = text
            selectedLetter = ""
            viewModel.filterData(text)
        }
    }

    fun startVoiceSearch() = speechLauncher.launch(
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sema unachotafuta ...")
        }
    )

    // ── Outer column so filter chips are outside the overlay Box ─────────────
    Column(modifier = modifier.fillMaxSize()) {

        SearchFieldRow(
            query = searchQuery,
            placeholder = "Tafuta kwenye Kamusi ...",
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

        // ── Box only wraps the scrollable area so VerticalLetters aligns here ─
        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 70.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selectedType == "MANENO") {
                    item { SectionHeader("Matokeo", words.size) }
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
                if (selectedType == "NAHAU") {
                    item { SectionHeader("Matokeo", idioms.size) }
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
                if (selectedType == "METHALI") {
                    item { SectionHeader("Matokeo", proverbs.size) }
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
                if (selectedType == "MISEMO") {
                    item { SectionHeader("Matokeo", sayings.size) }
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

            // VerticalLetters now overlays only the list area, below the chips
            VerticalLetters(
                selectedLetter = selectedLetter,
                onLetterSelected = { letter ->
                    selectedLetter = letter
                    searchQuery = letter
                    viewModel.filterData(letter)
                    scope.launch { listState.animateScrollToItem(0) }
                },
            )

            // FAB stack — scroll-to-top sits above the main FAB
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End,
            ) {
                AnimatedVisibility(
                    visible = showScrollToTop,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    SmallFloatingActionButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Rudi Juu")
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.ADVSEARCH) },
                    expanded = isAtTop,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    icon = { Icon(Icons.Filled.ManageSearch, "Tafuta kwa Kina") },
                    text = { Text("Tafuta kwa Kina") },
                )
            }
        }
    }
}