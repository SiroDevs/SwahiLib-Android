package com.swahilib.presentation.screens.init

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.domain.entities.UiState
import com.swahilib.presentation.components.indicators.EmptyState
import com.swahilib.presentation.components.indicators.ErrorState
import com.swahilib.presentation.components.indicators.LoadingState
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.InitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(
    viewModel: InitViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }
    val progress by viewModel.progress.collectAsState(initial = 0)
    val status by viewModel.status.collectAsState(initial = "Inahifadhi data")

    if (fetchData == 0) {
        viewModel.fetchData()
        fetchData++
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState == UiState.Saved) {
            navController.navigate(Routes.HOME)
        }
    }

    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = ThemeColors.accent2)
            ) {
                when (uiState) {
                    is UiState.Error -> ErrorState(
                        message = (uiState as UiState.Error).message,
                        onRetry = { viewModel.fetchData() }
                    )

                    is UiState.Loading -> LoadingState(
                        title = "Inapakia data ...",
                        fileName = "bar-loader",
                    )

                    is UiState.Saving ->
                        LoadingState(
                            title = status,
                            fileName = "opener-loading",
                            showProgress = true,
                            progressValue = progress
                        )

                    is UiState.Loaded -> {
                        viewModel.saveData()
                    }

                    else -> EmptyState()
                }
            }
        },
    )
}
