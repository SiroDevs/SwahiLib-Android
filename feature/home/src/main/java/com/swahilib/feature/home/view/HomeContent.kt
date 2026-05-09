package com.swahilib.feature.home.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.HomeTab
import com.swahilib.core.common.entity.homeTabs
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.listitems.IdiomItem
import com.swahilib.core.ui.components.listitems.ProverbItem
import com.swahilib.core.ui.components.listitems.SayingItem
import com.swahilib.core.ui.components.listitems.WordItem
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.CustomTabTitles
import com.swahilib.feature.home.components.SearchBox
import com.swahilib.feature.home.components.VerticalLetters

@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedLetter by rememberSaveable { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    val lastTabIndex = viewModel.lastHomeTab
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(lastTabIndex) }
    val selectedTab = homeTabs[selectedTabIndex]

    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())
    val words by viewModel.filteredWords.collectAsState(initial = emptyList())

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item { Box(modifier = Modifier.height(5.dp)) }
            item {
                SearchBox(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        selectedLetter = ""
                        viewModel.filterData(selectedTab, it)
                    },
                    onAdvancedSearch = { navController.navigate(Routes.ADVSEARCH) },
                    onClear = {
                        searchQuery = ""
                        viewModel.filterData(selectedTab, "")
                    }
                )
            }

            item {
                CustomTabTitles(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        val tabIndex = homeTabs.indexOf(tab)
                        selectedTabIndex = tabIndex
                        viewModel.filterData(tab, "")
                    }
                )
            }

            when (selectedTab) {
                HomeTab.Idioms -> {
                    if (idioms.isNotEmpty()) {
                        items(idioms) { idiom ->
                            IdiomItem(
                                idiom = idiom,
                                showSidebar = true,
                                onTap = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("idiom", idiom)
                                    navController.navigate(Routes.IDIOM)
                                },
                            )
                        }
                    } else {
                        item { EmptyState(message = "Hamna misemo") }
                    }
                }

                HomeTab.Proverbs -> {
                    if (proverbs.isNotEmpty()) {
                        items(proverbs) { proverb ->
                            ProverbItem(
                                proverb = proverb,
                                showSidebar = true,
                                onTap = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("proverb", proverb)
                                    navController.navigate(Routes.PROVERB)
                                }
                            )
                        }
                    } else {
                        item { EmptyState(message = "Hamna maneno") }
                    }
                }

                HomeTab.Sayings -> {
                    if (sayings.isNotEmpty()) {
                        items(sayings) { saying ->
                            SayingItem(
                                saying = saying,
                                showSidebar = true,
                                onTap = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("saying", saying)
                                    navController.navigate(Routes.SAYING)
                                }
                            )
                        }
                    } else {
                        item { EmptyState(message = "Hamna misemo") }
                    }
                }

                HomeTab.Words -> {
                    if (words.isNotEmpty()) {
                        items(words) { word ->
                            WordItem(
                                word = word,
                                showSidebar = true,
                                onTap = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("word", word)
                                    navController.navigate(Routes.WORD)
                                }
                            )
                        }
                    } else {
                        item { EmptyState(message = "Hamna maneno") }
                    }
                }
            }
        }
    }

    VerticalLetters(
        selectedLetter = selectedLetter,
        onLetterSelected = { letter ->
            selectedLetter = letter
            viewModel.filterData(selectedTab, letter)
        },
    )
}