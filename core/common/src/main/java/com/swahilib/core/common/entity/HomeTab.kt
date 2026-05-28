package com.swahilib.core.common.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeTab(var title: String, var icon: ImageVector) {
    object Search  : HomeTab("Tafuta", Icons.Default.Search)
    object Likes   : HomeTab("Vipendwa", Icons.Default.Favorite)
    object History : HomeTab("Historia", Icons.Default.History)
}

val homeTabs = listOf(
    HomeTab.Search,
    HomeTab.Likes,
    HomeTab.History,
)
