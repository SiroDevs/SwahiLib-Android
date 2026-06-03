package com.swahilib.feature.home.view.tabs

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.HomeSearchResults
import com.swahilib.feature.home.components.SearchFieldRow
import com.swahilib.feature.home.components.VerticalLetters
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeSearch(
    viewModel: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    prefsRepo: PrefsRepo,
    onShowDonation: () -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedLetter by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("MANENO") }
    val types = listOf("MANENO", "NAHAU", "METHALI", "MISEMO")

    val showDonation = remember { prefsRepo.shouldShowDonation() }
    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())
    val words by viewModel.filteredWords.collectAsState(initial = emptyList())

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val isAtTop by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
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
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sema unachotafuta ...")
        }
    )

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

        Box(modifier = Modifier.fillMaxSize()) {

            HomeSearchResults(
                selectedType = selectedType,
                words = words,
                idioms = idioms,
                proverbs = proverbs,
                sayings = sayings,
                showDonation = showDonation,
                listState = listState,
                viewModel = viewModel,
                navController = navController,
                onShowDonation = onShowDonation
            )


            VerticalLetters(
                selectedLetter = selectedLetter,
                onLetterSelected = { letter ->
                    selectedLetter = letter
                    searchQuery = letter
                    viewModel.filterData(letter)
                    scope.launch { listState.animateScrollToItem(0) }
                },
            )

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
