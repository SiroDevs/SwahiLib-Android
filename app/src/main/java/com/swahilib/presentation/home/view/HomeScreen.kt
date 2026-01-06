package com.swahilib.presentation.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.navigation.NavHostController
import com.revenuecat.purchases.ui.revenuecatui.*
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter
import com.swahilib.domain.entity.*
import com.swahilib.presentation.components.action.*
import com.swahilib.presentation.home.HomeViewModel
import com.swahilib.presentation.home.components.*
import com.swahilib.presentation.navigation.Routes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val lastTabIndex = viewModel.lastHomeTab
    var selectedTabIndex by rememberSaveable { mutableStateOf(lastTabIndex) }
    val selectedTab = homeTabs[selectedTabIndex]

    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedLetter by rememberSaveable { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    val canShowPaywall by viewModel.canShowPaywall.collectAsState()
    var showPaywall by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item {
                SearchBox(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        selectedLetter = ""
                        viewModel.filterData(selectedTab, it)
                    },
                    onAdvancedSearch = { navController.navigate(Routes.ADVSEARCH) },
                    onClear = {
                        searchQuery = ""
                        viewModel.filterData(selectedTab, "")
                    }
                )
            }
            item {
                CustomTabTitles(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        val tabIndex = homeTabs.indexOf(tab)
                        selectedTabIndex = tabIndex
                        selectedLetter = ""
                        viewModel.filterData(homeTabs[tabIndex], "")
                    },
                )
            }

            item {
                HomeContent(
                    viewModel = viewModel,
                    navController = navController,
                    selectedTab = selectedTab,
                    selectedLetter = selectedLetter,
                    onLetterSelected = { letter ->
                        selectedLetter = letter
                        viewModel.filterData(selectedTab, letter)
                    }
                )
            }
        }
    }
}
