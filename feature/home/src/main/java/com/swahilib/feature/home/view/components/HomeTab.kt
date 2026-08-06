package com.swahilib.feature.home.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Home's own pager tabs. Likes and History used to be tabs here too, but now live in their own
 * modules reached via app-bar actions; ChemshaBongo (the engagement dashboard) is a separate,
 * hard-coded bottom-nav item in HomeScreen that navigates away rather than swiping to a page, so
 * it isn't part of this sealed class.
 */
sealed class HomeTab(var title: String, var icon: ImageVector) {
    object Search : HomeTab("Tafuta", Icons.Default.Search)
}

val homeTabs = listOf(
    HomeTab.Search,
)
