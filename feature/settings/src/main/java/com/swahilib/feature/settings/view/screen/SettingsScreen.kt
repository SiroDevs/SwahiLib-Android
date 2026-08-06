package com.swahilib.feature.settings.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.AppTopBar

/**
 * Settings landing menu. Each row pushes its own sub-screen (Mwonekano / Arifa / Data Yako)
 * rather than showing everything inline on one long scroll.
 */
@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mipangilio ya SwahiLib",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ListItem(
                leadingContent = { Icon(Icons.Default.Palette, contentDescription = null) },
                headlineContent = { Text("Mwonekano") },
                supportingContent = { Text("Mandhari ya Kitumizi") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate(Routes.SETTINGS_APPEARANCE) },
            )
            HorizontalDivider()

            ListItem(
                leadingContent = { Icon(Icons.Default.NotificationsActive, contentDescription = null) },
                headlineContent = { Text("Arifa (Notifications)") },
                supportingContent = { Text("Neno, methali, changamoto na muhtasari wa wiki") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate(Routes.SETTINGS_NOTIFICATIONS) },
            )
            HorizontalDivider()

            ListItem(
                leadingContent = { Icon(Icons.Default.Storage, contentDescription = null) },
                headlineContent = { Text("Data Yako") },
                supportingContent = { Text("Dhibiti data iliyohifadhiwa kwenye kifaa chako") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate(Routes.SETTINGS_DATA) },
            )
            HorizontalDivider()
        }
    }
}
