package com.swahilib.feature.home.view.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.NotificationReminderBanner
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.feature.home.view.components.DailyHighlightsDialog
import com.swahilib.feature.home.view.components.HomeNavDrawer
import com.swahilib.feature.home.view.components.HomeSkeleton
import com.swahilib.feature.home.view.components.HomeTab
import com.swahilib.feature.home.view.components.homeTabs
import com.swahilib.feature.home.view.screen.tabs.HomeEngage
import com.swahilib.feature.home.view.screen.tabs.HomeSearch
import com.swahilib.feature.home.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    deepLinkRoute: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showDailyDialog by remember { mutableStateOf(false) }
    val dailyHighlights by viewModel.dailyHighlights.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = homeTabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { homeTabs.size }
    )

    LaunchedEffect(Unit) { viewModel.fetchData() }

    LaunchedEffect(Unit) {
        viewModel.loadDailyHighlights()
        if (prefsRepo.shouldShowDailyDialog()) {
            showDailyDialog = true
            prefsRepo.markDailyDialogShown()
        }
    }

    LaunchedEffect(deepLinkRoute, uiState) {
        if (deepLinkRoute != null && uiState is UiState.Filtered) {
            when (deepLinkRoute) {
                Routes.DAILY_PROVERB -> navController.navigate(Routes.DAILY_PROVERB)
                Routes.DAILY_WORD -> navController.navigate(Routes.DAILY_WORD)
                Routes.PROGRESS -> navController.navigate(Routes.PROGRESS)
                Routes.CHALLENGES -> navController.navigate(Routes.CHALLENGES)
                Routes.STATISTICS -> navController.navigate(Routes.STATISTICS)
                Routes.ACHIEVEMENTS -> navController.navigate(Routes.ACHIEVEMENTS)
                else -> navController.navigate(Routes.DAILY_WORD)
            }
            onDeepLinkConsumed()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(homeTabs[pagerState.currentPage])
    }

    LaunchedEffect(selectedTab) {
        val idx = homeTabs.indexOf(selectedTab)
        if (idx >= 0 && pagerState.currentPage != idx) pagerState.animateScrollToPage(idx)
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        val handle = currentBackStackEntry?.savedStateHandle
        val pending = handle?.get<String>("pendingSearchQuery")
        if (pending != null) {
            viewModel.requestSearch(pending)
            handle.remove<String>("pendingSearchQuery")
        }
    }

    HomeNavDrawer(
        drawerState = drawerState,
        onNavigate = { navController.navigate(it) }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "SwahiLib",
                    tagline = "Kamusi ya Kiswahili",
                    centered = true,
                    showNavDrawer = true,
                    onNavIconClick = { scope.launch { drawerState.open() } },
                    actions = {
                        when (selectedTab) {
                            HomeTab.Search -> {
                                IconButton(onClick = { navController.navigate(Routes.LIKES) }) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Vipendwa")
                                }
                                IconButton (onClick = { navController.navigate(Routes.HISTORY) }) {
                                    Icon(Icons.Default.History, contentDescription = "Historia")
                                }
                            }

                            HomeTab.Library -> {
                                // No extra actions for the Library catalogue tab.
                            }

                            HomeTab.Engage -> {
                                IconButton (onClick = { navController.navigate(Routes.CHALLENGES) }) {
                                    Icon(Icons.Default.TrackChanges, contentDescription = "Changamoto")
                                }
                                IconButton (onClick = { navController.navigate(Routes.STATISTICS) }) {
                                    Icon(Icons.Default.InsertChart, contentDescription = "Takwimu")
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.onPrimary) {
                    homeTabs.forEach { homeTab ->
                        NavigationBarItem(
                            icon = { Icon(homeTab.icon, contentDescription = homeTab.title) },
                            label = { Text(homeTab.title) },
                            selected = selectedTab == homeTab,
                            onClick = { viewModel.setSelectedTab(homeTab) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                NotificationReminderBanner(
                    prefsRepo = prefsRepo,
                    onGoToSettings = { navController.navigate(Routes.SETTINGS) },
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                                        onShowDonation = { navController.navigate(Routes.DONATION) },
                                    )

                                    HomeTab.Library -> {
                                        val libraryViewModel: LibraryViewModel = hiltViewModel()
                                        HomeLibrary(
                                            navController = navController,
                                            viewModel = libraryViewModel,
                                        )
                                    }

                                    HomeTab.Engage -> HomeEngage(
                                        navController = navController,
                                    )
                                }
                            }
                        }

                        is UiState.Error -> ErrorState(
                            message = (uiState as UiState.Error).message,
                            onRetry = { viewModel.fetchData() }
                        )

                        is UiState.Loading -> HomeSkeleton()
                        else -> EmptyState()
                    }
                } // Box
            } // Column
        }
    }

    if (showDailyDialog) {
        DailyHighlightsDialog(
            highlights = dailyHighlights,
            onDismiss = { showDailyDialog = false },
            onWordClick = {
                showDailyDialog = false
                navController.navigate(Routes.DAILY_WORD)
            },
            onProverbClick = {
                showDailyDialog = false
                navController.navigate(Routes.DAILY_PROVERB)
            },
        )
    }
}
