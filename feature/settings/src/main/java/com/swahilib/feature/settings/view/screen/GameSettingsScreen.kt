package com.swahilib.feature.settings.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.settings.viewmodel.GameSettingsViewModel

/**
 * Music/SFX mute switches for the engagement games (ChemshaBongo) - the same
 * switches also live behind the sound icon on each game's own top bar, so
 * either place stays in sync since both read/write the same prefs.
 */
@Composable
fun GameSettingsScreen(
    navController: NavHostController,
    viewModel: GameSettingsViewModel,
) {
    val soundPlayer = viewModel.soundPlayer
    var musicOn by remember { mutableStateOf(soundPlayer.musicEnabled) }
    var sfxOn by remember { mutableStateOf(soundPlayer.sfxEnabled) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Michezo (ChemshaBongo)",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ListItem(
                leadingContent = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                headlineContent = { Text("Muziki wa Mchezo") },
                supportingContent = { Text("Muziki wa nyuma unapocheza mchezo") },
                trailingContent = {
                    Switch(checked = musicOn, onCheckedChange = { musicOn = it; soundPlayer.musicEnabled = it })
                },
            )
            ListItem(
                leadingContent = { Icon(Icons.Default.VolumeUp, contentDescription = null) },
                headlineContent = { Text("Sauti za Kugusa (SFX)") },
                supportingContent = { Text("Sauti fupi za vitendo kama vile kugusa na kuwasilisha") },
                trailingContent = {
                    Switch(checked = sfxOn, onCheckedChange = { sfxOn = it; soundPlayer.sfxEnabled = it })
                },
            )
        }
    }
}
