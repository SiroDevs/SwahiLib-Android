package com.swahilib.presentation.screens.init

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.domain.entities.UiState
import com.swahilib.presentation.components.*
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.viewmodels.InitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(
    viewModel: InitViewModel,
    navController: NavHostController,
) {
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchWords()
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
                    .background(color = MaterialTheme.colorScheme.surface)
            ) {
                when (uiState) {
                    is UiState.Error -> ErrorState(
                        errorMessage = (uiState as UiState.Error).errorMessage,
                        onRetry = { viewModel.fetchWords() }
                    )

                    is UiState.Loading -> LoadingState("Loading data ...")
                    is UiState.Saving -> LoadingState("Saving data ...")

                    is UiState.Loaded -> {
                        viewModel.saveData()
                    }

                    else -> EmptyState()
                }
            }
        },
    )
}