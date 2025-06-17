package com.swahilib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.data.sample.SampleWords
import com.swahilib.domain.entities.HomeTab
import com.swahilib.domain.entities.homeTabs
import com.swahilib.presentation.components.action.*
import com.swahilib.presentation.screens.home.widgets.HomeSurface
import com.swahilib.presentation.components.listitems.WordItem
import com.swahilib.presentation.screens.home.lists.WordsList
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    var selectedTab by remember { mutableStateOf(homeTabs[0]) }

    fun setSelectedTab(tab: HomeTab) {
        selectedTab = tab
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CustomTabTitles(
            selectedTab = selectedTab,
            onTabSelected = { }
        )
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.width(60.dp)
            ) {
                VerticalLetters()
            }

            HomeSurface() {
                when (selectedTab) {
                    HomeTab.Words -> WordsList(viewModel, navController)
                    HomeTab.Idioms -> WordsList(viewModel, navController)
                    HomeTab.Proverbs -> WordsList(viewModel, navController)
                    HomeTab.Sayings -> WordsList(viewModel, navController)
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = ThemeColors.primary
                )
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
                VerticalLetters()
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
