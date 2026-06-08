package com.swahilib.feature.home.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.common.utils.AppConstants
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    prefsRepo: PrefsRepo,
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = AppConstants.APP_TITLE,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = AppConstants.APP_TAGLINE,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                // Daily content items at top
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = null) },
                    label = { Text("Neno la Siku") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.NENO_LA_SIKU)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.FormatQuote, contentDescription = null) },
                    label = { Text("Methali ya Siku") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.METHALI_YA_SIKU)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Navigation items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Mipangilio") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.SETTINGS)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Jinsi ya Kutumia") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.HOW_IT_WORKS)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
                    label = { Text("Usaidizi na Maoni") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.HELP)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null) },
                    label = { Text("Changa SwahiLib") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.DONATION)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.weight(1f))
                Text(
                    text = AppConstants.APP_CREDITS,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "SwahiLib",
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menyu")
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
                    UiState.Loading -> LoadingState(fileName = "circle-loader")
                    else -> EmptyState()
                }
            }
        }
    }
}
