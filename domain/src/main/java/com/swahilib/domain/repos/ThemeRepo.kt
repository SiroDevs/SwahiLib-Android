package com.swahilib.domain.repos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
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
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@HiltViewModel
class ThemeRepository @Inject constructor(
    private val prefs: PrefsRepo
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