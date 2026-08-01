package com.swahilib.core.design_system.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.core.data.repos.ThemeMode

@Composable
fun ThemeSelectorDialog(
    current: ThemeMode,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    var selected by remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chagua Mandhari") },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = mode },
                    ) {
                        RadioButton(selected = selected == mode, onClick = { selected = mode })
                        Text(
                            appThemeName(mode),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onThemeSelected(selected); onDismiss() }) {
                Text("Sawa")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Ghairi") }
        },
    )
}

fun appThemeName(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> "Chaguo la Mfumo (System)"
    ThemeMode.LIGHT -> "Mandhari ya Nuru (Light Theme)"
    ThemeMode.DARK -> "Mandhari ya Giza (Dark Theme)"
}
