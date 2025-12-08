package com.swahilib.presentation.home.view.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.navigation.NavHostController
import com.swahilib.domain.entity.UiState
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.components.listitems.IdiomItem
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.home.HomeViewModel

@Composable
fun IdiomsList(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val idioms by viewModel.filteredIdioms.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is UiState.Filtered -> {
                when {
                    idioms.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 5.dp)
                        ) {
                            itemsIndexed(idioms) { index, idiom ->
                                IdiomItem(
                                    idiom = idiom,
                                    onTap = {
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("idiom", idiom)
                                        navController.navigate(Routes.IDIOM)
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
