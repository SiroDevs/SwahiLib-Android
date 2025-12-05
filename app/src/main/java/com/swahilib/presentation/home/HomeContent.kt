package com.swahilib.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.data.sample.SampleWords
import com.swahilib.domain.entity.*
import com.swahilib.presentation.screens.home.components.*
import com.swahilib.presentation.components.listitems.*
import com.swahilib.presentation.screens.home.lists.*
import com.swahilib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedTab: HomeTab,
    selectedLetter: String,
    onTabSelected: (HomeTab) -> Unit,
    onLetterSelected: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomTabTitles(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.width(75.dp)
            ) {
                VerticalLetters(
                    selectedLetter = selectedLetter,
                    onLetterSelected = onLetterSelected
                )

            }

            HomeSurface() {
                when (selectedTab) {
                    HomeTab.Idioms -> IdiomsList(viewModel, navController)
                    HomeTab.Proverbs -> ProverbsList(viewModel, navController)
                    HomeTab.Sayings -> SayingsList(viewModel, navController)
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
            Box(modifier = Modifier.width(60.dp)) {
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
