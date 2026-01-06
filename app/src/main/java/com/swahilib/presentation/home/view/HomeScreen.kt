package com.swahilib.presentation.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.window.*
import androidx.navigation.NavHostController
import com.revenuecat.purchases.ui.revenuecatui.*
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter
import com.swahilib.domain.entity.UiState
import com.swahilib.domain.entity.homeTabs
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

    val lastTabIndex = viewModel.lastHomeTab
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by rememberSaveable { mutableStateOf(lastTabIndex) }
    val selectedTab = homeTabs[selectedTabIndex]

    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())
    val words by viewModel.filteredWords.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchData()
        showPaywall = canShowPaywall
    }

    if (showPaywall) {
        Dialog(
            onDismissRequest = { showPaywall = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val paywallOptions = remember {
                PaywallOptions.Builder(dismissRequest = { showPaywall = false })
                    .setShouldDisplayDismissButton(true)
                    .build()
            }
            Box() {
                if (canShowPaywall) {
                    Paywall(paywallOptions)
                } else {
                    CustomerCenter(onDismiss = { showPaywall = false })
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState) {
                is UiState.Filtered -> {
                    HomeContent(
                        selectedTab = selectedTab,
                        idioms = idioms,
                        proverbs = proverbs,
                        sayings = sayings,
                        words = words,
                        viewModel = viewModel,
                        navController = navController,
                        onTabSelected = { tab ->
                            val tabIndex = homeTabs.indexOf(tab)
                            selectedTabIndex = tabIndex
                            viewModel.filterData(tab, "")
                        }
                    )
                }

                is UiState.Error -> {
                    ErrorState(
                        message = (uiState as UiState.Error).message,
                        onRetry = { }
                    )
                }

                UiState.Loading -> LoadingState(fileName = "circle-loader" )
                else -> EmptyState()
            }
        }
    }
}
