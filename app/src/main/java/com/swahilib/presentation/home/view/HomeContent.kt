package com.swahilib.presentation.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.swahilib.data.models.*
import com.swahilib.domain.entity.*
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.components.listitems.*
import com.swahilib.presentation.home.HomeViewModel
import com.swahilib.presentation.home.components.*
import com.swahilib.presentation.navigation.Routes

@Composable
fun HomeContent(
    selectedTab: HomeTab,
    idioms: List<Idiom>,
    proverbs: List<Proverb>,
    sayings: List<Saying>,
    words: List<Word>,
    viewModel: HomeViewModel,
    navController: NavHostController,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedLetter by rememberSaveable { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                onTabSelected = onTabSelected,
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.width(75.dp)
                ) {
                    VerticalLetters(
                        selectedLetter = selectedLetter,
                        onLetterSelected = { letter ->
                            selectedLetter = letter
                            viewModel.filterData(selectedTab, letter)
                        }
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 20.dp)),
                    tonalElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.LightGray.copy(alpha = 0.1f),
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                                    )
                                )
                            )
                    ) { }
                }
            }
        }

        when (selectedTab) {
            HomeTab.Idioms -> {
                if (idioms.isNotEmpty()) {
                    items(
                        items = idioms,
                        key = { it.rid }
                    ) { idiom ->
                        IdiomItem(
                            idiom = idiom,
                            onTap = {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("idiom", idiom)
                                navController.navigate(Routes.IDIOM)
                            },
                        )
                    }
                } else { item { EmptyState( message = "Hamna misemo" ) } }
            }

            HomeTab.Proverbs -> {
                if (proverbs.isNotEmpty()) {
                    items(
                        items = proverbs,
                        key = { it.rid }
                    ) { proverb ->
                        ProverbItem(
                            proverb = proverb,
                            onTap = {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("proverb", proverb)
                                navController.navigate(Routes.PROVERB)
                            }
                        )
                    }
                } else { item { EmptyState( message = "Hamna maneno" ) } }
            }
            HomeTab.Sayings -> {
                if (sayings.isNotEmpty()) {
                    items(
                        items = sayings,
                        key = { it.rid }
                    ) { saying ->
                        SayingItem(
                            saying = saying,
                            onTap = {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("saying", saying)
                                navController.navigate(Routes.SAYING)
                            }
                        )
                    }
                } else { item { EmptyState( message = "Hamna misemo" ) } }
            }
            HomeTab.Words -> {
                if (words.isNotEmpty()) {
                    items(
                        items = words,
                        key = { it.rid }
                    ) { word ->
                        WordItem(
                            word = word,
                            onTap = {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("word", word)
                                navController.navigate(Routes.WORD)
                            }
                        )
                    }
                } else { item { EmptyState( message = "Hamna maneno" ) } }
            }
        }
    }
}