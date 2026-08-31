package com.swahilib.feature.library.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.TileAction
import com.swahilib.core.ui.components.action.TileActionCard
import com.swahilib.feature.library.viewmodel.LibraryViewModel

fun resolveLibraryIcon(name: String): ImageVector = when (name) {
    "Groups" -> Icons.Default.Groups
    "Checkroom" -> Icons.Default.Checkroom
    "SetMeal" -> Icons.Default.SetMeal
    "BugReport" -> Icons.Default.BugReport
    "Water" -> Icons.Default.Water
    "Casino" -> Icons.Default.Casino
    "WavingHand" -> Icons.Default.WavingHand
    "FormatQuote" -> Icons.Default.FormatQuote
    "Public" -> Icons.Default.Public
    else -> Icons.Default.MenuBook
}

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
            val tile = TileAction(
                title = config.title,
                icon = resolveLibraryIcon(config.iconName),
                route = Routes.libraryCollection(config.key),
            )
            TileActionCard(
                tile = tile,
                onClick = { navController.navigate(tile.route) },
            )
        }
    }
}