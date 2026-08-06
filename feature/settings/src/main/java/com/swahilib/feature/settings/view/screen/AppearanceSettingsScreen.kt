package com.swahilib.feature.settings.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material3.Icon
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
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.design_system.theme.ThemeSelectorDialog
import com.swahilib.core.design_system.theme.appThemeName
import com.swahilib.core.ui.components.action.AppTopBar

@Composable
fun AppearanceSettingsScreen(
    navController: NavHostController,
    themeRepo: ThemeRepo,
) {
    val theme = themeRepo.selectedTheme
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            current = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { themeRepo.setTheme(it); showThemeDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mwonekano",
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
                leadingContent = { Icon(Icons.Default.Brightness6, contentDescription = null) },
                headlineContent = { Text("Mandhari") },
                supportingContent = { Text(appThemeName(theme)) },
                modifier = Modifier.clickable { showThemeDialog = true },
            )
        }
    }
}
