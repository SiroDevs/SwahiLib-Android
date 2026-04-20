package com.swahilib.presentation.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.swahilib.domain.entity.*
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.components.listitems.*
import com.swahilib.presentation.home.HomeViewModel
import com.swahilib.presentation.home.components.*
import com.swahilib.presentation.navigation.Routes

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
    var selectedTabIndex by rememberSaveable { mutableStateOf(lastTabIndex) }
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