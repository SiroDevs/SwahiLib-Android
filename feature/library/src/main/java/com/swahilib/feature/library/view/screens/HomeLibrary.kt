package com.swahilib.feature.library.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.feature.library.view.components.LibraryCollectionCard
import com.swahilib.feature.library.viewmodel.LibraryViewModel

@Composable
fun HomeLibrary(
    navController: NavHostController,
    viewModel: LibraryViewModel,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(viewModel.collections, key = { it.key }) { config ->
            LibraryCollectionCard(
                config = config,
                onClick = { navController.navigate(Routes.libraryCollection(config.key)) },
            )
        }
    }
}
