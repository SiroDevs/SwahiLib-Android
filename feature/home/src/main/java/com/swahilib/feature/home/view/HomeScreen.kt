package com.swahilib.feature.home.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.NotificationReminderBanner
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.feature.home.HomeViewModel
import com.swahilib.feature.home.components.HomeNavDrawer
import com.swahilib.feature.home.components.HomeSkeleton
import com.swahilib.feature.home.components.HomeTab
import com.swahilib.feature.home.components.homeTabs
import com.swahilib.feature.home.view.tabs.HomeHistory
import com.swahilib.feature.home.view.tabs.HomeSearch
import com.swahilib.feature.home.view.tabs.HomeLikes
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    deepLinkRoute: String? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = homeTabs.indexOf(selectedTab).coerceAtLeast(0),
        pageCount = { homeTabs.size }
    )

    LaunchedEffect(Unit) { viewModel.fetchData() }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setSelectedTab(homeTabs[pagerState.currentPage])
    }

    LaunchedEffect(selectedTab) {
        val idx = homeTabs.indexOf(selectedTab)
        if (idx >= 0 && pagerState.currentPage != idx) pagerState.animateScrollToPage(idx)
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
                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
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
}
