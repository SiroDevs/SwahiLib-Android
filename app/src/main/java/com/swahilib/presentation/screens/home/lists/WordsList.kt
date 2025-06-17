package com.swahilib.presentation.screens.home.lists


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.navigation.NavHostController
import com.swahilib.domain.entities.UiState
import com.swahilib.presentation.components.indicators.EmptyState
import com.swahilib.presentation.components.listitems.WordItem
import com.swahilib.presentation.viewmodels.HomeViewModel

@Composable
fun WordsList(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val words by viewModel.filteredWords.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is UiState.Filtered ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    items(words) { word ->
                        WordItem(
                            word = word,
                            onTap = { },
                        )
                    }
                }

            else -> EmptyState()
        }
    }
}
