package com.swahilib.presentation.screens.home

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swahilib.core.utils.Preferences
import com.swahilib.domain.entities.UiState
import com.swahilib.domain.entities.homeTabs
import com.swahilib.presentation.components.action.*
import com.swahilib.presentation.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current
    var fetchData by rememberSaveable { mutableStateOf(0) }

    if (fetchData == 0) {
        viewModel.fetchData()
        fetchData++
    }

    val prefs =
        context.getSharedPreferences(Preferences.PREFERENCE_FILE, Context.MODE_PRIVATE)
    val lastTab = prefs.getInt(Preferences.LAST_HOME_TAB, 0)

    val uiState by viewModel.uiState.collectAsState()

    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(homeTabs[lastTab]) }
    var selectedLetter by remember { mutableStateOf("") }

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
                    title = "SwahiLib - Kamusi ya Kiswahili",
                    actions = {
                        if (uiState != UiState.Loading) {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                        }
                    }
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            HomeContent(
                viewModel = viewModel,
                navController = navController,
                selectedTab = selectedTab,
                selectedLetter = selectedLetter,
                onTabSelected = { tab ->
                    selectedTab = tab
                    selectedLetter = ""
                },
                onLetterSelected = { letter ->
                    selectedLetter = letter
                    viewModel.filterData(selectedTab, letter)
                }
            )
        }
    }
}
