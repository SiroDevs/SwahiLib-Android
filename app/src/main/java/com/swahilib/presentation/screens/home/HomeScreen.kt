package com.swahilib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.swahilib.domain.entities.UiState
import com.swahilib.presentation.components.ErrorState
import com.swahilib.presentation.components.LoadingState
import com.swahilib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchData()
        fetchData++
    }

    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val isRefreshing = uiState is UiState.Loading
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { }
    )

    Scaffold() { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            when (uiState) {
                is UiState.Error -> ErrorState(
                    errorMessage = (uiState as UiState.Error).errorMessage,
                    onRetry = { viewModel.fetchData() }
                )

                UiState.Loading -> LoadingState(
                    title = "Loading data ",
                    fileName = "opener-loading",
                )

                else -> {
                    HomeContent(
                        viewModel = viewModel,
                        navController = navController,
                        selectedTab = selectedTab,
                        isRefreshing = isRefreshing,
                        pullRefreshState = pullRefreshState
                    )
                }
            }
        }
    }
}
