package com.swahilib.feature.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material3.HorizontalDivider
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
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.designsystem.theme.ThemeSelectorDialog
import com.swahilib.core.designsystem.theme.appThemeName
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.settings.SettingsViewModel
import com.swahilib.feature.settings.components.ConfirmResetDialog
import com.swahilib.feature.settings.components.SettingsSectionTitle

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    themeRepo: ThemeRepo,
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

//            SettingsSectionTitle("CHANGIA SWAHILIB")
//            ListItem(
//                leadingContent = {
//                    Icon(
//                        Icons.Default.Brightness6, contentDescription = ""
//                    )
//                },
//                headlineContent = { Text("Changa Hivi Sasa") },
//                supportingContent = { Text("Tunahitaji mchango wako ili tuzudi kukuhudumia") },
//                modifier = Modifier.clickable { navController.navigate(Routes.DONATION) },
//            )
//            HorizontalDivider()
        }
    }
}
