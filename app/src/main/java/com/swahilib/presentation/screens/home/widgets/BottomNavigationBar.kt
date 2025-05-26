package com.swahilib.presentation.screens.home.widgets

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun BottomNavigationBar(
    selectedItem: HomeNavItem,
    onItemSelected: (HomeNavItem) -> Unit
) {
    val items = listOf(
        HomeNavItem.Search,
        HomeNavItem.Likes,
    )
    BottomNavigation(
        backgroundColor = ThemeColors.primary,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Black,
                alwaysShowLabel = true,
                selected = selectedItem == item,
                onClick = { onItemSelected(item) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        selectedItem = HomeNavItem.Search,
        onItemSelected = {}
    )
}

sealed class HomeNavItem(var icon: ImageVector, var title: String) {
    object Search : HomeNavItem(Icons.Default.Search, "Search")
    object Likes : HomeNavItem(Icons.Default.FavoriteBorder, "Likes")
}