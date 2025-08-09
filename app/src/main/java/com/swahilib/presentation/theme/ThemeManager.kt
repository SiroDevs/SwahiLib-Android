package com.swahilib.presentation.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@HiltViewModel
class ThemeManager @Inject constructor() : ViewModel() {
    var selectedTheme by mutableStateOf(ThemeMode.SYSTEM)
        private set

    fun setTheme(mode: ThemeMode) {
        selectedTheme = mode
    }
}

@Composable
fun ThemeSelectorDialog(
    current: ThemeMode,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit
) {
    var selectedTheme by remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Theme") },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTheme = mode }
                    ) {
                        RadioButton(
                            selected = selectedTheme == mode,
                            onClick = { selectedTheme = mode }
                        )
                        Text(
                            appThemeName(mode),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onThemeSelected(selectedTheme)
                    onDismiss()
                }
            ) {
                Text("OKAY")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun appThemeName(mode: ThemeMode):String {
    return when (mode){
        ThemeMode.SYSTEM -> "System Default"
        ThemeMode.LIGHT -> "Light Theme"
        ThemeMode.DARK -> "Dark Theme"
    }
}