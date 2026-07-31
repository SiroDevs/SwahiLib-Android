package com.swahilib.core.ui.components.general

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.swahilib.core.data.notifications.NotificationPermission
import com.swahilib.core.data.repos.PrefsRepo

private object NotifBannerSessionState {
    var dismissedThisSession = false
}

fun shouldShowNotifBanner(prefsRepo: PrefsRepo, context: android.content.Context): Boolean {
    if (NotifBannerSessionState.dismissedThisSession) return false
    val permDenied = !NotificationPermission.isGranted(context)
    val bothEnabled = prefsRepo.wordNotifEnabled && prefsRepo.proverbNotifEnabled
    return permDenied || !bothEnabled
}

@Composable
fun NotificationReminderBanner(
    prefsRepo: PrefsRepo,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var visible by remember { mutableStateOf(shouldShowNotifBanner(prefsRepo, context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Permission just granted – schedule both workers if they were enabled in prefs.
            // Also re-evaluate banner visibility.
        }
        visible = shouldShowNotifBanner(prefsRepo, context)
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp),
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Arifa Zimelemazwa",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Wezesha Arifa (Notifications) za Neno la Siku na Methali ya Siku ili upate kujifunza kila asubuhi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        when {
                            NotificationPermission.isGranted(context) -> onGoToSettings()
                            NotificationPermission.canRequestRuntimePermission() ->
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            else ->
                                // No runtime dialog available (pre-Android 13, or the
                                // permission was already permanently denied) - send the
                                // user straight to the system notification settings.
                                NotificationPermission.openAppNotificationSettings(context)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Wezesha",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = {
                        NotifBannerSessionState.dismissedThisSession = true
                        visible = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Baadaye",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}
