package com.swahilib.feature.help.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.AppConstants
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavHostController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var attachedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var titleError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var showDonationPrompt by remember { mutableStateOf(false) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            attachedUris = (attachedUris + uris).distinct().take(5)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Usaidizi na Maoni",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tuko hapa kukusaidia!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Ukipata tatizo au una maoni, tujulishe. Jaza fomu hii na tutajibu haraka iwezekanavyo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text("Kichwa *") },
                placeholder = { Text("Muhtasari mfupi wa tatizo au pendekezo lako") },
                isError = titleError,
                supportingText = if (titleError) ({ Text("Kichwa depthhitajika") }) else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it; descriptionError = false },
                label = { Text("Maelezo *") },
                placeholder = { Text("Eleza tatizo au pendekezo lako kwa undani...") },
                isError = descriptionError,
                supportingText = if (descriptionError) ({ Text("Maelezo yanahitajika") }) else null,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                maxLines = 8,
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                text = "Picha au Video (Sio lazima)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedCard(
                modifier = Modifier.fillMaxWidth().clickable { pickMediaLauncher.launch("image/*") },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Ambatanisha picha", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Text("Hadi faili 5", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (attachedUris.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    attachedUris.forEachIndexed { index, uri ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(text = uri.lastPathSegment ?: "faili_${index + 1}", modifier = Modifier.weight(1f), maxLines = 1, style = MaterialTheme.typography.bodySmall)
                            IconButton(onClick = { attachedUris = attachedUris - uri }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Ondoa", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Button(
                onClick = {
                    titleError = title.isBlank()
                    descriptionError = description.isBlank()
                    if (titleError || descriptionError) return@Button

                    val deviceInfo = buildString {
                        appendLine("---")
                        appendLine("Maelezo ya Kifaa:")
                        appendLine("Model: ${Build.MODEL} (${Build.MANUFACTURER})")
                        appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                    }

                    val emailBody = buildString {
                        appendLine("Kichwa: $title"); appendLine()
                        appendLine("Maelezo:"); appendLine(description); appendLine()
                        append(deviceInfo)
                    }

                    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(AppConstants.SUPPORT_EMAIL))
                        putExtra(Intent.EXTRA_SUBJECT, "SwahiLib: $title")
                        putExtra(Intent.EXTRA_TEXT, emailBody)
                        if (attachedUris.isNotEmpty()) {
                            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(attachedUris))
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    }
                    context.startActivity(Intent.createChooser(intent, "Tuma barua pepe kupitia"))
                    showDonationPrompt = true
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Wasiliana Nasi", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    if (showDonationPrompt) {
        AlertDialog(
            onDismissRequest = { showDonationPrompt = false },
            title = { Text("Asante!") },
            text = { Text("Je ungependa kuchangia SwahiLib?\n\nMchango (Donation) wako utatusaidia pakubwa katika kazi hii ya kukuhudmia.") },
            confirmButton = {
                Button(onClick = {
                    showDonationPrompt = false
                    navController.navigate(Routes.DONATION)
                }) { Text("NDIO") }
            },
            dismissButton = {
                TextButton(onClick = { showDonationPrompt = false }) { Text("Baadaye") }
            },
        )
    }
}
