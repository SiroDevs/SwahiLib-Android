package com.swahilib.presentation.screens.home.likes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.swahilib.domain.entities.UiState
import com.swahilib.presentation.components.EmptyState
import com.swahilib.presentation.components.listitems.SearchSongItem
import com.swahilib.presentation.viewmodels.HomeViewModel

@Composable
fun LikesScreen(
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val likes by viewModel.books.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        when (uiState) {
            is UiState.Filtered ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    items(likes) { song ->
                        EmptyState()
                        /*SearchSongItem(
                            song = song,
                            onClick = { },
                            isSelected = false,
                            isSearching = false,
                        )*/
                    }
                }

            else -> EmptyState()
        }
    }
}