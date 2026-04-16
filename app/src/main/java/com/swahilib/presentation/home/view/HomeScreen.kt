package com.swahilib.presentation.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.window.*
import androidx.navigation.NavHostController
import com.revenuecat.purchases.ui.revenuecatui.*
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter
import com.swahilib.domain.entity.UiState
import com.swahilib.presentation.components.action.*
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.home.HomeViewModel
import com.swahilib.presentation.navigation.Routes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val canShowPaywall by viewModel.canShowPaywall.collectAsState()
    var showPaywall by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchData()
        showPaywall = canShowPaywall
    }

    if (showPaywall) {
        Dialog(
            onDismissRequest = { showPaywall = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val paywallOptions = remember {
                    PaywallOptions.Builder(dismissRequest = { showPaywall = false })
                        .setShouldDisplayDismissButton(true)
                        .build()
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    if (canShowPaywall) {
                        Paywall(paywallOptions)
                    } else {
                        CustomerCenter(onDismiss = { showPaywall = false })
                    }
                }
            }
        }
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