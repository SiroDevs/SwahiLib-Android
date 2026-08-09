package com.swahilib.feature.home.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeTab(var title: String, var icon: ImageVector) {
    object Search : HomeTab("Tafuta", Icons.Default.Search)
    object Engage : HomeTab("Chemsha Bongo", Icons.Default.EmojiEvents)
}

val homeTabs = listOf(
    HomeTab.Search,
    HomeTab.Engage,
)
