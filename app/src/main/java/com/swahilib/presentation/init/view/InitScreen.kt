package com.swahilib.presentation.init.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.domain.entity.UiState
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.init.InitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(
    viewModel: InitViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

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
                    .background(color = MaterialTheme.colorScheme.onPrimary)
            ) {
                when (uiState) {
                    is UiState.Error -> ErrorState(
                        message = (uiState as UiState.Error).message,
                        onRetry = { viewModel.fetchData() }
                    )

                    is UiState.Loading -> LoadingState(
                        fileName = "opener-loading",
                    )

                    else -> EmptyState()
                }
            }
        },
    )
}
