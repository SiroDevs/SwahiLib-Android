package com.swahilib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.data.sample.SampleWords
import com.swahilib.domain.entities.*
import com.swahilib.presentation.components.action.*
import com.swahilib.presentation.screens.home.widgets.HomeSurface
import com.swahilib.presentation.components.listitems.WordItem
import com.swahilib.presentation.screens.home.lists.WordsList
import com.swahilib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    var selectedTab by remember { mutableStateOf(homeTabs[0]) }
    var selectedLetter by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomTabTitles(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
            }
        )
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.width(75.dp)
            ) {
                VerticalLetters(
                    selectedLetter = selectedLetter,
                    onLetterSelected = { letter ->
                        selectedLetter = letter
                        viewModel.filterByLetter(selectedTab, letter)
                    }
                )

            }

            HomeSurface() {
                when (selectedTab) {
                    HomeTab.Idioms -> WordsList(viewModel, navController)
                    HomeTab.Proverbs -> WordsList(viewModel, navController)
                    HomeTab.Sayings -> WordsList(viewModel, navController)
                    HomeTab.Words -> WordsList(viewModel, navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomTabTitles(
            selectedTab = HomeTab.Words,
            onTabSelected = {},
        )
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box( modifier = Modifier.width(60.dp) ) {
                VerticalLetters(
                    selectedLetter = "A",
                    onLetterSelected = {}
                )
            }

            HomeSurface() {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 5.dp),
                    contentPadding = PaddingValues(horizontal = 5.dp)
                ) {
                    items(SampleWords) { word ->
                        WordItem(
                            word = word,
                            onTap = { },
                        )
                    }
                }
            }
        }
    }
}
