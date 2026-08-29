package com.swahilib.feature.settings.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.AppTopBar

@Composable
fun SettingsScreen(navController: NavHostController) {
    var showMoreMenu by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mipangilio ya SwahiLib",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { showMoreMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(expanded = showMoreMenu, onDismissRequest = { showMoreMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Reset App Data") },
                            leadingIcon = { Icon(Icons.Default.DataUsage, null) },
                            onClick = {
                                showMoreMenu = false
                                navController.navigate(Routes.SETTINGS_DATA)
                            },
                        )
                    }
                }
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
                leadingContent = { Icon(Icons.Default.SportsEsports, contentDescription = null) },
                headlineContent = { Text("Michezo (ChemshaBongo)") },
                supportingContent = { Text("Muziki, sauti za mchezo, na mazoezi") },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                modifier = Modifier.clickable { navController.navigate(Routes.SETTINGS_GAMES) },
            )
            HorizontalDivider()
        }
    }
}
