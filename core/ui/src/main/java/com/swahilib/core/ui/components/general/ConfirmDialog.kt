package com.swahilib.core.ui.components.general

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/** Simple Ndio/Hapana confirmation dialog, reused wherever a destructive action needs confirming. */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String = "Ndio",
    dismissLabel: String = "Hapana",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(dismissLabel) }
        },
    )
}
