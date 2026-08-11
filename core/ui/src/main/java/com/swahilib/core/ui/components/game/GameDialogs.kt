package com.swahilib.core.ui.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.general.ConfirmDialog

@Composable
fun GameRestartDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ConfirmDialog(
        title = "Je, unataka kuanza upya?",
        message = "Kuanza upya kutafuta mwendelezo (progress) wako.",
        confirmLabel = "Ndio",
        dismissLabel = "La",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
fun GameExitDialog(
    onGoBackDiscard: () -> Unit,
    onSaveAndGoBack: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Je unataka kuachia njiani?") },
        text = {
            Column {
                Text(
                    text = "Iwapo unataka kurudi nyuma unaweza kuhifadhi mwendelezo wako ama ufute kabisa.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSaveAndGoBack,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Hifadhi na Rudi Nyuma")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onGoBackDiscard,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Rudi Nyuma")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Ghairi") }
        },
    )
}
