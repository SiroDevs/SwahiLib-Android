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
import com.swahilib.presentation.components.listitems.SayingItem
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.viewmodels.HomeViewModel

@Composable
fun SayingsList(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val sayings by viewModel.filteredSayings.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is UiState.Filtered -> {
                when {
                    sayings.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 5.dp)
                        ) {
                            itemsIndexed(sayings) { index, saying ->
                                SayingItem(
                                    saying = saying,
                                    onTap = {
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("saying", saying)
                                        navController.navigate(Routes.SAYING)
                                    },
                                    modifier = Modifier
                                        .then(if (index == 0) Modifier.padding(top = 5.dp) else Modifier)
                                )
                            }
                        }
                    }

                    else -> LoadingState(
                        title = "",
                        fileName = "circle-loader"
                    )
                }
            }

            else -> LoadingState(
                title = "",
                fileName = "circle-loader"
            )
        }
    }
}
