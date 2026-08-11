package com.swahilib.core.ui.components.share

import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ScreenshotReminderDialog(onShareClick: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                uri ?: return
                val isScreenshot = try {
                    context.contentResolver.query(
                        uri,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            arrayOf(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                MediaStore.Images.Media.DISPLAY_NAME,
                            )
                        else
                            arrayOf(MediaStore.Images.Media.DISPLAY_NAME),
                        null, null, null,
                    )?.use { cursor ->
                        if (!cursor.moveToFirst()) return@use false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val path = cursor.getString(0) ?: ""
                            path.contains("screenshot", ignoreCase = true)
                        } else {
                            val name = cursor.getString(0) ?: ""
                            name.contains("screenshot", ignoreCase = true)
                        }
                    } ?: false
                } catch (e: Exception) {
                    false
                }
                if (isScreenshot) showDialog = true
            }
        }
        context.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer,
        )
        onDispose { context.contentResolver.unregisterContentObserver(observer) }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Kushiriki ni Bora",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            },
            text = {
                Text(
                    text = "Tuna njia bora ya kushiriki kwa njia ya maandishi ama picha.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(onClick = { showDialog = false; onShareClick() }) {
                    Text("Shiriki")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Sawa") }
            },
        )
    }
}
