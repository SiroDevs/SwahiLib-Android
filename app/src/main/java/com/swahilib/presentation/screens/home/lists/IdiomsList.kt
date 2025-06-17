package com.swahilib.presentation.screens.home.lists


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.navigation.NavHostController
import com.swahilib.domain.entities.UiState
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.components.listitems.IdiomItem
import com.swahilib.presentation.viewmodels.HomeViewModel

@Composable
fun IdiomsList(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val words by viewModel.filteredIdioms.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is UiState.Filtered ->
                if (words.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 5.dp)
                    ) {
                        itemsIndexed(words) { index, word ->
                            IdiomItem(
                                word = word,
                                onTap = { },
                                modifier = if (index == 0) Modifier.padding(top = 5.dp) else Modifier
                            )
                        }
                    }
                } else {
                    EmptyState()
                }


            else -> LoadingState(
                title = "",
                fileName = "circle-loader",
            )
        }
    }
}
