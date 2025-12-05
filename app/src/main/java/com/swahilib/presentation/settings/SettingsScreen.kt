package com.swahilib.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.domain.repository.*
import com.swahilib.presentation.components.action.AppTopBar
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.screens.settings.components.*
import com.swahilib.presentation.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    themeRepo: ThemeRepository,
) {
    val theme = themeRepo.selectedTheme
    var showThemeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    fun navigateToSplash() {
        navController.navigate(Routes.SPLASH) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    if (showResetDialog) {
        ConfirmResetDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                showResetDialog = false
                viewModel.clearData()
                navigateToSplash()
            }
        )
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            current = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = {
                themeRepo.setTheme(it)
                showThemeDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mipangilio",
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
            SettingsSectionTitle("Mwonekano")
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.Brightness6, contentDescription = ""
                    )
                },
                headlineContent = { Text("Mandhari") },
                supportingContent = { Text(appThemeName(theme)) },
                modifier = Modifier.clickable { showThemeDialog = true },
            )
            HorizontalDivider()

            /*SettingsSectionTitle("Selection")
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.EditNote, contentDescription = "Reset"
                    )
                },
                headlineContent = { Text("Modify Collection") },
                supportingContent = { Text("Add or Remove Songbooks") },
                modifier = Modifier.clickable {
                    viewModel.updateSelection(true)
                    navigateToSplash()
                },
            )
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Default.Refresh, contentDescription = "Reset"
                    )
                },
                headlineContent = { Text("Select Afresh") },
                supportingContent = { Text("Reset everything and start over") },
                modifier = Modifier.clickable { showResetDialog = true },
            )
            HorizontalDivider()*/
        }
    }
}
