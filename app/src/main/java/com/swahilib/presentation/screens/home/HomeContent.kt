package com.swahilib.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.domain.entities.UiState
import com.swahilib.presentation.components.*
import com.swahilib.presentation.components.action.*
import com.swahilib.presentation.screens.home.likes.LikesScreen
import com.swahilib.presentation.screens.home.search.SearchScreen
import com.swahilib.presentation.screens.home.widgets.*
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    navController: NavHostController,
    selectedTab: HomeNavItem,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "SwahiLib - Kamusi ya Kiswahili",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primary,
                modifier = Modifier.padding(5.dp)
            )
            CustomTabTitles()
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.width(60.dp)
                ) {
                    VerticalLetters()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 24.dp))
                        .background(ThemeColors.accent2),
                    color = ThemeColors.accent3,
                    tonalElevation = 4.dp
                ) {

                }
            }

        }
    }
//    when (uiState) {
//        is UiState.Error -> ErrorState(
//            errorMessage = (uiState as UiState.Error).errorMessage,
//            onRetry = { viewModel.fetchData() }
//        )
//
//        UiState.Loading -> LoadingState(
//            title = "Loading data ",
//            fileName = "opener-loading",
//        )
//
//        else -> {
//            Box(
//                modifier = Modifier
//                    .pullRefresh(pullRefreshState)
//            ) {
//                when (selectedTab) {
//                    HomeNavItem.Search -> SearchScreen(viewModel, navController)
//                    HomeNavItem.Likes -> LikesScreen(viewModel)
//                }
//
//                PullRefreshIndicator(
//                    refreshing = isRefreshing,
//                    state = pullRefreshState,
//                    modifier = Modifier.align(Alignment.TopCenter),
//                    contentColor = ThemeColors.primary
//                )
//            }
//        }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "SwahiLib - Kamusi ya Kiswahili",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primary,
                modifier = Modifier.padding(5.dp)
            )
            CustomTabTitles()
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.width(60.dp)
                ) {
                    VerticalLetters()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 24.dp))
                        .background(ThemeColors.accent2),
                    color = ThemeColors.accent3,
                    tonalElevation = 4.dp
                ) {

                }
            }

        }
    }
}
