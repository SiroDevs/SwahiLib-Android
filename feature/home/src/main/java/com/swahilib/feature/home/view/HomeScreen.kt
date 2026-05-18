package com.swahilib.feature.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.*
import com.swahilib.core.ui.components.indicators.*
import com.swahilib.feature.home.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "SwahiLib",
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "")
                    }
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState) {
                is UiState.Filtered -> {
                    HomeContent(
                        viewModel = viewModel,
                        navController = navController,
                    )
                }

                is UiState.Error -> {
                    ErrorState(
                        message = (uiState as UiState.Error).message,
                        onRetry = {
                            viewModel.fetchData()
                        }
                    )
                }

                UiState.Loading -> LoadingState(fileName = "circle-loader")
                else -> EmptyState()
            }
        }
    }
}