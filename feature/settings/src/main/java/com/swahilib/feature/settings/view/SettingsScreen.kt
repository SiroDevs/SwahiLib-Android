package com.swahilib.feature.settings.view

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val context = LocalContext.current
    val theme = themeRepo.selectedTheme
    var showThemeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val nenoEnabled   by viewModel.nenoEnabled.collectAsState()
    val nenoHour      by viewModel.nenoHour.collectAsState()
    val nenoMinute    by viewModel.nenoMinute.collectAsState()
    val methaliEnabled by viewModel.methaliEnabled.collectAsState()
    val methaliHour   by viewModel.methaliHour.collectAsState()
    val methaliMinute by viewModel.methaliMinute.collectAsState()

    fun formatTime(h: Int, m: Int) = "%02d:%02d".format(h, m)

    fun showTimePicker(initialH: Int, initialM: Int, onSet: (Int, Int) -> Unit) =
        TimePickerDialog(context, { _, h, m -> onSet(h, m) }, initialH, initialM, true).show()

    if (showResetDialog) {
        ConfirmResetDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
//                showResetDialog = false
//                viewModel.clearData()
//                popUpTo(0) { inclusive = true }
//                launchSingleTop = true
            }
        )
    }

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
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionTitle("Mwonekano")
            ListItem(
                leadingContent = { Icon(Icons.Default.Brightness6, contentDescription = null) },
                headlineContent = { Text("Mandhari") },
                supportingContent = { Text(appThemeName(theme)) },
                modifier = Modifier.clickable { showThemeDialog = true },
            )
            HorizontalDivider()

            SettingsSectionTitle("Neno la Siku")
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = if (nenoEnabled) Icons.Default.NotificationsActive
                                      else Icons.Default.NotificationsOff,
                        contentDescription = null
                    )
                },
                headlineContent  = { Text("Arifa ya Neno la Siku") },
                supportingContent = {
                    Text(
                        if (nenoEnabled) "Imewezeshwa · ${formatTime(nenoHour, nenoMinute)}"
                        else "Imezimwa"
                    )
                },
                trailingContent = {
                    Switch(checked = nenoEnabled, onCheckedChange = { viewModel.setNenoEnabled(it) })
                },
            )
            if (nenoEnabled) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.AutoStories, contentDescription = null) },
                    headlineContent  = { Text("Wakati wa Arifa") },
                    supportingContent = { Text(formatTime(nenoHour, nenoMinute)) },
                    modifier = Modifier.clickable {
                        showTimePicker(nenoHour, nenoMinute) { h, m -> viewModel.setNenoTime(h, m) }
                    },
                )
            }
            HorizontalDivider()

            SettingsSectionTitle("Methali ya Siku")
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = if (methaliEnabled) Icons.Default.NotificationsActive
                                      else Icons.Default.NotificationsOff,
                        contentDescription = null
                    )
                },
                headlineContent  = { Text("Arifa ya Methali ya Siku") },
                supportingContent = {
                    Text(
                        if (methaliEnabled) "Imewezeshwa · ${formatTime(methaliHour, methaliMinute)}"
                        else "Imezimwa"
                    )
                },
                trailingContent = {
                    Switch(checked = methaliEnabled, onCheckedChange = { viewModel.setMethaliEnabled(it) })
                },
            )
            if (methaliEnabled) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.FormatQuote, contentDescription = null) },
                    headlineContent  = { Text("Wakati wa Arifa") },
                    supportingContent = { Text(formatTime(methaliHour, methaliMinute)) },
                    modifier = Modifier.clickable {
                        showTimePicker(methaliHour, methaliMinute) { h, m -> viewModel.setMethaliTime(h, m) }
                    },
                )
            }
            HorizontalDivider()

            SettingsSectionTitle("Changia SwahiLib")
            ListItem(
                leadingContent = { Icon(Icons.Default.VolunteerActivism, contentDescription = null) },
                headlineContent  = { Text("Changa Hivi Sasa") },
                supportingContent = { Text("Tunahitaji mchango wako ili tuzidi kukuhudumia") },
                modifier = Modifier.clickable { navController.navigate(Routes.DONATION) },
            )
            HorizontalDivider()

            SettingsSectionTitle("Data")
            ListItem(
                headlineContent = {
                    Text(
                        "Weka Upya Data",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                supportingContent = { Text("Futa data yote na uanze upya") },
                modifier = Modifier.clickable { showResetDialog = true },
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}
