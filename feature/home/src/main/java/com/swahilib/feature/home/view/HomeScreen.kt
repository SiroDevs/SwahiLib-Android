package com.swahilib.feature.home.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.HomeTab
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.common.entity.homeTabs
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.designsystem.theme.ThemeSelectorDialog
import com.swahilib.core.ui.components.action.*
import com.swahilib.core.ui.components.indicators.*
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.view.tabs.HomeHistoria
import com.swahilib.feature.home.view.tabs.HomeTafuta
import com.swahilib.feature.home.view.tabs.HomeVipendwa

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    themeRepo: ThemeRepo,
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    val theme = themeRepo.selectedTheme

    val tabs = listOf(HomeTab.Search, HomeTab.Likes, HomeTab.History)
    val pagerState = rememberPagerState(
        initialPage = tabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { tabs.size }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(tabs[pagerState.currentPage])
    }

    LaunchedEffect(selectedTab) {
        val idx = tabs.indexOf(selectedTab)
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
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
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
                            leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
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
                tabs.forEach { tab ->
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
                        when (tabs[page]) {
                            HomeTab.Search -> HomeTafuta(
                                viewModel = viewModel,
                                navController = navController,
                            )
                            HomeTab.Likes -> HomeVipendwa(
                                viewModel = viewModel,
                                navController = navController,
                            )
                            HomeTab.History -> HomeHistoria(
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
