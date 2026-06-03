package com.swahilib.feature.home.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.designsystem.theme.ThemeSelectorDialog
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.core.ui.components.indicators.LoadingState
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.HomeTab
import com.swahilib.feature.home.components.homeTabs
import com.swahilib.feature.home.view.tabs.HomeHistory
import com.swahilib.feature.home.view.tabs.HomeSearch
import com.swahilib.feature.home.view.tabs.HomeLikes

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    themeRepo: ThemeRepo,
    prefsRepo: PrefsRepo,
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showDonationDialog by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme

    val pagerState = rememberPagerState(
        initialPage = homeTabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { homeTabs.size }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(homeTabs[pagerState.currentPage])
    }

    LaunchedEffect(selectedTab) {
        val idx = homeTabs.indexOf(selectedTab)
        if (idx >= 0 && pagerState.currentPage != idx) {
            pagerState.animateScrollToPage(idx)
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            current = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = {
                themeRepo.setTheme(it)
                showThemeDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "SwahiLib",
                actions = {
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(Icons.Filled.Brightness6, contentDescription = "Mandhari")
                    }
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Zaidi")
                    }
                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mipangilio") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMoreMenu = false
                                navController.navigate(Routes.SETTINGS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Jinsi ya Kutumia") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            onClick = {
                                showMoreMenu = false
                                navController.navigate(Routes.HOW_IT_WORKS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Usaidizi na Maoni") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.HelpOutline,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showMoreMenu = false
                                navController.navigate(Routes.HELP)
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.onPrimary) {
                homeTabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == tab,
                        onClick = { viewModel.setSelectedTab(tab) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState) {
                is UiState.Filtered -> {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = true,
                    ) { page ->
                        when (homeTabs[page]) {
                            HomeTab.Search -> HomeSearch(
                                viewModel = viewModel,
                                navController = navController,
                                prefsRepo = prefsRepo,
                                onShowDonationDialog = { navController.navigate(Routes.DONATION) },
                            )

                            HomeTab.Likes -> HomeLikes(
                                viewModel = viewModel,
                                navController = navController,
                            )

                            HomeTab.History -> HomeHistory(
                                viewModel = viewModel,
                                navController = navController,
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorState(
                        message = (uiState as UiState.Error).message,
                        onRetry = { viewModel.fetchData() }
                    )
                }

                UiState.Loading -> LoadingState(fileName = "circle-loader")
                else -> EmptyState()
            }
        }
    }
}
