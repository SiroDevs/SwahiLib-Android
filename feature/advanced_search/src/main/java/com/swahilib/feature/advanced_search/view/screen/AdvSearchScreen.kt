package com.swahilib.feature.advanced_search.view.screen

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.advanced_search.viewmodel.AdvSearchViewModel
import com.swahilib.feature.advanced_search.viewmodel.SearchMode
import com.swahilib.feature.advanced_search.viewmodel.SortOrder
import com.swahilib.feature.advanced_search.view.components.idiomsSection
import com.swahilib.feature.advanced_search.view.components.EmptySearchPrompt
import com.swahilib.feature.advanced_search.view.components.proverbsSection
import com.swahilib.feature.advanced_search.view.components.ResultCountBadge
import com.swahilib.feature.advanced_search.view.components.sayingsSection
import com.swahilib.feature.advanced_search.view.components.SearchModeMenu
import com.swahilib.feature.advanced_search.view.components.SortDropdown
import com.swahilib.feature.advanced_search.view.components.TypeFilterRow
import com.swahilib.feature.advanced_search.view.components.wordsSection
import com.swahilib.feature.home.view.components.SearchFieldRow
import java.util.Locale

private val TYPES = listOf("YOTE", "MANENO", "NAHAU", "METHALI", "MISEMO")

@Composable
fun AdvSearchScreen(
    navController: NavHostController,
    viewModel: AdvSearchViewModel,
    prefsRepo: PrefsRepo,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("YOTE") }
    var sortOrder by rememberSaveable { mutableStateOf(SortOrder.AZ) }
    var searchMode by rememberSaveable { mutableStateOf(SearchMode.BEGINNING) }
    val showDonation = remember { prefsRepo.shouldShowDonation() }

    val words by viewModel.filteredWords.collectAsState(initial = emptyList())
    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())

    val totalResults = when (selectedType) {
        "MANENO" -> words.size
        "NAHAU" -> idioms.size
        "METHALI" -> proverbs.size
        "MISEMO" -> sayings.size
        else -> words.size + idioms.size + proverbs.size + sayings.size
    }

    fun reFilter() = viewModel.filterData(query, sortOrder, searchMode)

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            query = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: ""
            reFilter()
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

    LaunchedEffect(Unit) { viewModel.fetchData() }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Tafuta Kamusi kwa Kina",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    SearchModeMenu(
                        current = searchMode,
                        onSelect = { searchMode = it; reFilter() }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SearchFieldRow(
                    modifier = Modifier.weight(1f),
                    query = query,
                    placeholder = "Tafuta ...",
                    onQueryChange = { query = it; reFilter() },
                    onClear = { query = ""; reFilter() },
                    onVoiceSearch = { startVoiceSearch() }
                )
                SortDropdown(
                    current = sortOrder,
                    onSelect = { sortOrder = it; reFilter() }
                )
            }

            TypeFilterRow(types = TYPES, selected = selectedType, onSelect = { selectedType = it })

            ResultCountBadge(query = query, count = totalResults)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (query.isEmpty()) {
                    item { EmptySearchPrompt() }
                } else {
                    wordsSection(
                        words = words,
                        query = query,
                        show = selectedType == "YOTE" || selectedType == "MANENO",
                        navController = navController,
                        viewModel = viewModel,
                        showDonation = showDonation,
                        onShowDonation = { navController.navigate(Routes.DONATION) },
                    )
                    idiomsSection(
                        idioms = idioms, query = query,
                        show = selectedType == "YOTE" || selectedType == "NAHAU",
                        navController = navController, viewModel = viewModel,
                        showDonation = showDonation,
                        onShowDonation = {
                            navController.navigate(
                                Routes.DONATION
                            )
                        },
                    )
                    proverbsSection(
                        proverbs = proverbs,
                        query = query,
                        show = selectedType == "YOTE" || selectedType == "METHALI",
                        navController = navController,
                        viewModel = viewModel,
                        showDonation = showDonation,
                        onShowDonation = { navController.navigate(Routes.DONATION) },
                    )
                    sayingsSection(
                        sayings = sayings,
                        query = query,
                        show = selectedType == "YOTE" || selectedType == "MISEMO",
                        navController = navController,
                        viewModel = viewModel,
                        showDonation = showDonation,
                        onShowDonation = { navController.navigate(Routes.DONATION) },
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
