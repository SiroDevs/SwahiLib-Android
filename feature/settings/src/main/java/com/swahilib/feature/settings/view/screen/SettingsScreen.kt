package com.swahilib.feature.settings.view.screen

import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Summarize
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
import com.swahilib.core.data.notifications.NotificationPermission
import com.swahilib.core.data.repos.ThemeRepo
import com.swahilib.core.design_system.theme.ThemeSelectorDialog
import com.swahilib.core.design_system.theme.appThemeName
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.settings.viewmodel.SettingsViewModel
import com.swahilib.feature.settings.view.components.ConfirmResetDialog
import com.swahilib.feature.settings.view.components.SettingsSectionTitle

private enum class NotifToggle { NENO, METHALI, CHANGAMOTO, MUHTASARI }

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

    val wordEnabled by viewModel.wordEnabled.collectAsState()
    val wordHour by viewModel.wordHour.collectAsState()
    val wordMinute by viewModel.wordMinute.collectAsState()
    val proverbEnabled by viewModel.proverbEnabled.collectAsState()
    val proverbHour by viewModel.proverbHour.collectAsState()
    val proverbMinute by viewModel.proverbMinute.collectAsState()
    val challengeEnabled by viewModel.challengeEnabled.collectAsState()
    val challengeHour by viewModel.challengeHour.collectAsState()
    val challengeMinute by viewModel.challengeMinute.collectAsState()
    val summaryEnabled by viewModel.summaryEnabled.collectAsState()
    val summaryHour by viewModel.summaryHour.collectAsState()
    val summaryMinute by viewModel.summaryMinute.collectAsState()

    fun formatTime(h: Int, m: Int) = "%02d:%02d".format(h, m)

    fun showTimePicker(initialH: Int, initialM: Int, onSet: (Int, Int) -> Unit) =
        TimePickerDialog(context, { _, h, m -> onSet(h, m) }, initialH, initialM, true).show()

    // Which switch is waiting on the outcome of a system permission prompt.
    var pendingToggle by remember { mutableStateOf<NotifToggle?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        when (pendingToggle) {
            NotifToggle.NENO -> viewModel.setNenoEnabled(granted)
            NotifToggle.METHALI -> viewModel.setMethaliEnabled(granted)
            NotifToggle.CHANGAMOTO -> viewModel.setChallengeEnabled(granted)
            NotifToggle.MUHTASARI -> viewModel.setSummaryEnabled(granted)
            null -> {}
        }
        pendingToggle = null
    }

    fun requestEnable(toggle: NotifToggle, enable: (Boolean) -> Unit) {
        when {
            NotificationPermission.isGranted(context) -> enable(true)
            NotificationPermission.canRequestRuntimePermission() -> {
                pendingToggle = toggle
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                NotificationPermission.openAppNotificationSettings(context)
            }
        }
    }

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
                        imageVector = if (wordEnabled) Icons.Default.NotificationsActive
                        else Icons.Default.NotificationsOff,
                        contentDescription = null
                    )
                },
                headlineContent = { Text("Arifa ya Neno la Siku") },
                supportingContent = {
                    Text(
                        if (wordEnabled) "Imewezeshwa · ${formatTime(wordHour, wordMinute)}"
                        else "Imezimwa"
                    )
                },
                trailingContent = {
                    Switch(
                        checked = wordEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                requestEnable(NotifToggle.NENO) { viewModel.setNenoEnabled(it) }
                            } else {
                                viewModel.setNenoEnabled(false)
                            }
                        })
                },
            )
            if (wordEnabled) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.AutoStories, contentDescription = null) },
                    headlineContent = { Text("Wakati wa Arifa") },
                    supportingContent = { Text(formatTime(wordHour, wordMinute)) },
                    modifier = Modifier.clickable {
                        showTimePicker(wordHour, wordMinute) { h, m -> viewModel.setNenoTime(h, m) }
                    },
                )
            }
            HorizontalDivider()

            SettingsSectionTitle("Methali ya Siku")
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = if (proverbEnabled) Icons.Default.NotificationsActive
                        else Icons.Default.NotificationsOff,
                        contentDescription = null
                    )
                },
                headlineContent = { Text("Arifa ya Methali ya Siku") },
                supportingContent = {
                    Text(
                        if (proverbEnabled) "Imewezeshwa · ${
                            formatTime(
                                proverbHour,
                                proverbMinute
                            )
                        }"
                        else "Imezimwa"
                    )
                },
                trailingContent = {
                    Switch(
                        checked = proverbEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                requestEnable(NotifToggle.METHALI) { viewModel.setMethaliEnabled(it) }
                            } else {
                                viewModel.setMethaliEnabled(false)
                            }
                        })
                },
            )
            if (proverbEnabled) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.FormatQuote, contentDescription = null) },
                    headlineContent = { Text("Wakati wa Arifa") },
                    supportingContent = { Text(formatTime(proverbHour, proverbMinute)) },
                    modifier = Modifier.clickable {
                        showTimePicker(
                            proverbHour,
                            proverbMinute
                        ) { h, m -> viewModel.setMethaliTime(h, m) }
                    },
                )
            }
            HorizontalDivider()

            SettingsSectionTitle("Changamoto ya Siku")
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = if (challengeEnabled) Icons.Default.NotificationsActive
                        else Icons.Default.NotificationsOff,
                        contentDescription = null,
                    )
                },
                headlineContent = { Text("Kumbusho la Changamoto") },
                supportingContent = {
                    Text(
                        if (challengeEnabled) "Imewezeshwa · ${formatTime(challengeHour, challengeMinute)}"
                        else "Imezimwa"
                    )
                },
                trailingContent = {
                    Switch(
                        checked = challengeEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                requestEnable(NotifToggle.CHANGAMOTO) { viewModel.setChallengeEnabled(it) }
                            } else viewModel.setChallengeEnabled(false)
                        },
                    )
                },
            )
            if (challengeEnabled) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    headlineContent = { Text("Wakati wa Kumbusho") },
                    supportingContent = { Text(formatTime(challengeHour, challengeMinute)) },
                    modifier = Modifier.clickable {
                        showTimePicker(challengeHour, challengeMinute) { h, m ->
                            viewModel.setChallengeTime(h, m)
                        }
                    },
                )
            }
            HorizontalDivider()

            SettingsSectionTitle("Muhtasari wa Wiki")
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = if (summaryEnabled) Icons.Default.NotificationsActive
                        else Icons.Default.NotificationsOff,
                        contentDescription = null,
                    )
                },
                headlineContent = { Text("Muhtasari wa Kila Jumapili") },
                supportingContent = {
                    Text(
                        if (summaryEnabled) "Imewezeshwa · ${formatTime(summaryHour, summaryMinute)}"
                        else "Imezimwa"
                    )
                },
                trailingContent = {
                    Switch(
                        checked = summaryEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                requestEnable(NotifToggle.MUHTASARI) { viewModel.setSummaryEnabled(it) }
                            } else viewModel.setSummaryEnabled(false)
                        },
                    )
                },
            )
            if (summaryEnabled) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.Summarize, contentDescription = null) },
                    headlineContent = { Text("Wakati wa Muhtasari") },
                    supportingContent = { Text(formatTime(summaryHour, summaryMinute)) },
                    modifier = Modifier.clickable {
                        showTimePicker(summaryHour, summaryMinute) { h, m ->
                            viewModel.setSummaryTime(h, m)
                        }
                    },
                )
            }
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
