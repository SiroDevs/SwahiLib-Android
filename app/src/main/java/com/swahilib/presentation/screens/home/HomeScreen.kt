package com.swahilib.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.swahilib.domain.entity.UiState
import com.swahilib.domain.entity.homeTabs
import com.swahilib.presentation.components.action.*
import com.swahilib.presentation.viewmodels.HomeViewModel
import com.swahilib.core.helpers.NetworkUtils
import com.swahilib.presentation.navigation.Routes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current

    val lastTabIndex = viewModel.lastHomeTab
    var selectedTabIndex by rememberSaveable { mutableStateOf(lastTabIndex) }
    val selectedTab = homeTabs[selectedTabIndex]

    val uiState by viewModel.uiState.collectAsState()

    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedLetter by rememberSaveable { mutableStateOf("") }

    val isProUser by viewModel.isProUser.collectAsState()
    var showPaywall by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModel.checkSubscription()
            showPaywall = !isProUser
        }
    }

    if (showPaywall) {
        Dialog(
            onDismissRequest = { showPaywall = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PaywallSheet(
                isProUser = isProUser,
                onDismissRequest = {
                    showPaywall = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            if (isSearching) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        selectedLetter = ""
                        viewModel.filterData(selectedTab, it)
                    },
                    onClose = {
                        isSearching = false
                        searchQuery = ""
                        viewModel.filterData(selectedTab, "")
                    }
                )
            } else {
                AppTopBar(
                    title = "SwahiLib",
                    actions = {
                        if (uiState != UiState.Loading) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Filled.Search, contentDescription = "")
                            }
                        }
                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "")
                        }
                    }
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            HomeContent(
                viewModel = viewModel,
                navController = navController,
                selectedTab = selectedTab,
                selectedLetter = selectedLetter,
                onTabSelected = { tab ->
                    val tabIndex = homeTabs.indexOf(tab)
                    selectedTabIndex = tabIndex
                    selectedLetter = ""
                    viewModel.filterData(homeTabs[tabIndex], "")
                },
                onLetterSelected = { letter ->
                    selectedLetter = letter
                    viewModel.filterData(selectedTab, letter)
                }
            )
        }
    }
}
