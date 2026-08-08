package com.swahilib.feature.settings.view.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.ConfirmDialog
import com.swahilib.feature.settings.view.components.SettingsSectionTitle
import com.swahilib.feature.settings.viewmodel.DataSettingsViewModel

private enum class DataAction(val title: String, val description: String, val icon: ImageVector) {
    HISTORY(
        "Futa Historia",
        "Futa historia yote ya kutazama na kutafuta.",
        Icons.Default.History,
    ),
    LIKES(
        "Futa Vipendwa",
        "Ondoa moyo kwenye maneno, nahau, misemo na methali zote ulizopenda.",
        Icons.Default.Favorite,
    ),
    DAILY_CONTENT(
        "Futa Neno na Methali ya Kila Siku",
        "Futa historia ya neno na methali za siku zilizopita.",
        Icons.Default.AutoStories,
    ),
    ENGAGEMENT(
        "Futa ChemshaBongo",
        "Futa alama zako, mfuatano, beji na changamoto zote za ChemshaBongo.",
        Icons.Default.EmojiEvents,
    ),
    EVERYTHING(
        "Futa Kila Kitu",
        "Futa HISTORIA, VIPENDWA, DATA YA SIKU, na CHEMSHABONGO yote kwa pamoja.",
        Icons.Default.DeleteForever,
    ),
}

@Composable
fun DataSettingsScreen(
    navController: NavHostController,
    viewModel: DataSettingsViewModel = hiltViewModel(),
) {
    var pendingAction by remember { mutableStateOf<DataAction?>(null) }

    pendingAction?.let { action ->
        ConfirmDialog(
            title = "${action.title}?",
            message = "${action.description} Hatua hii haiwezi kutenduliwa.",
            onConfirm = {
                when (action) {
                    DataAction.HISTORY -> viewModel.clearHistory()
                    DataAction.LIKES -> viewModel.clearLikes()
                    DataAction.DAILY_CONTENT -> viewModel.clearDailyContent()
                    DataAction.ENGAGEMENT -> viewModel.clearEngagement()
                    DataAction.EVERYTHING -> viewModel.clearEverything()
                }
                pendingAction = null
            },
            onDismiss = { pendingAction = null },
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Data Yako",
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
            SettingsSectionTitle("Futa Data")
            listOf(DataAction.HISTORY, DataAction.LIKES, DataAction.DAILY_CONTENT, DataAction.ENGAGEMENT)
                .forEach { action ->
                    ListItem(
                        leadingContent = { Icon(action.icon, contentDescription = null) },
                        headlineContent = { Text(action.title) },
                        supportingContent = { Text(action.description) },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.clickable { pendingAction = action },
                    )
                }

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "ENEO LA HATARI",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        DataAction.EVERYTHING.description + " Hatua hii haiwezi kutenduliwa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { pendingAction = DataAction.EVERYTHING },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(DataAction.EVERYTHING.title)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
