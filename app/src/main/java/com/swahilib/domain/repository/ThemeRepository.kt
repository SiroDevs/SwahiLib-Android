package com.swahilib.domain.repository

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@HiltViewModel
class ThemeRepository @Inject constructor(
    private val prefs: PrefsRepository
) : ViewModel() {
    var selectedTheme by mutableStateOf(prefs.appThemeMode)
        private set

    fun setTheme(mode: ThemeMode) {
        prefs.appThemeMode = mode
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
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .clickable { selectedTheme = mode }
                    ) {
                        RadioButton(
                            selected = selectedTheme == mode,
                            onClick = { selectedTheme = mode }
                        )
                        Text(
                            appThemeName(mode),
                            modifier = Modifier.Companion.padding(start = 8.dp),
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
                Text("SAWA")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kataa")
            }
        }
    )
}

fun appThemeName(mode: ThemeMode):String {
    return when (mode){
        ThemeMode.SYSTEM -> "Chaguo la Mfumo"
        ThemeMode.LIGHT -> "Mandhari ya Nuru"
        ThemeMode.DARK -> "Mandhari ya Giza"
    }
}