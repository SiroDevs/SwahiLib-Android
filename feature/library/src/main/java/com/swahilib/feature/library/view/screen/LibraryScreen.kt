package com.swahilib.feature.library.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.swahilib.core.common.library.LibraryDisplayMode
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.feature.library.view.components.LibraryExpandableList
import com.swahilib.feature.library.view.components.LibraryGrid
import com.swahilib.feature.library.viewmodel.LibraryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavHostController,
    viewModel: LibraryViewModel,
    collectionKey: String,
) {
    val config = remember(collectionKey) { viewModel.configFor(collectionKey) }
    val items by viewModel.itemsFor(collectionKey).collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    var expandedId by remember(collectionKey) { mutableStateOf<Long?>(null) }

    LaunchedEffect(collectionKey) { viewModel.ensureLoaded(collectionKey) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = config?.title ?: "Maktaba ya SwahiLib",
                tagline = config?.subtitle,
                centered = true,
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                config == null -> Text(
                    "Haipatikani",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                items.isEmpty() && isSyncing -> CircularProgressIndicator()

                items.isEmpty() -> EmptyState()

                config.displayMode == LibraryDisplayMode.GRID -> LibraryGrid(
                    items = items,
                    isGrouped = config.isGrouped
                )

                else -> LibraryExpandableList(
                    items = items,
                    isGrouped = config.isGrouped,
                    expandedId = expandedId,
                    onToggle = { id -> expandedId = if (expandedId == id) null else id },
                )
            }
        }
    }
}
