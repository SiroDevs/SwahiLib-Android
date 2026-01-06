package com.swahilib.presentation.home.view.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavHostController
import com.swahilib.domain.entity.UiState
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.components.listitems.ProverbItem
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.home.HomeViewModel

@Composable
fun ProverbsList(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val proverbs by viewModel.filteredProverbs.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is UiState.Filtered -> {
                when {
                    proverbs.isNotEmpty() -> {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 5.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            proverbs.forEachIndexed { index, proverb ->
                                ProverbItem(
                                    proverb = proverb,
                                    onTap = {
                                        navController.currentBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("proverb", proverb)
                                        navController.navigate(Routes.PROVERB)
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
