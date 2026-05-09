package com.swahilib.feature.init.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.indicators.*
import com.swahilib.feature.init.InitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(
    viewModel: InitViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState == UiState.Saved) {
            navController.navigate(Routes.HOME)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        when (uiState) {
            is UiState.Error -> ErrorState(
                message = (uiState as UiState.Error).message,
                onRetry = { viewModel.initialize(context) }
            )

            is UiState.Loading -> LoadingState(
                fileName = "opener-loading",
            )

            else -> EmptyState()
        }
    }
}
