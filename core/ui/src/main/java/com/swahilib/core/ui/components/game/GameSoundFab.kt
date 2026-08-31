/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.ui.components.game

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun GameSoundFab(soundPlayer: GameSoundPlayer) {
    var expanded by remember { mutableStateOf(false) }
    var musicOn by remember { mutableStateOf(soundPlayer.musicEnabled) }
    var sfxOn by remember { mutableStateOf(soundPlayer.sfxEnabled) }

    Box {
        SmallFloatingActionButton(onClick = { expanded = true }) {
            Icon(
                imageVector = if (musicOn || sfxOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                contentDescription = "Sauti za Mchezo",
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Muziki wa Mchezo") },
                leadingIcon = { Icon(if (musicOn) Icons.Filled.MusicNote else Icons.Filled.MusicOff, contentDescription = null) },
                trailingIcon = {
                    Switch(checked = musicOn, onCheckedChange = {
                        musicOn = it
                        soundPlayer.musicEnabled = it
                    })
                },
                onClick = { musicOn = !musicOn; soundPlayer.musicEnabled = musicOn },
            )
            DropdownMenuItem(
                text = { Text("Sauti za Kugusa (SFX)") },
                leadingIcon = { Icon(Icons.Outlined.VolumeUp, contentDescription = null) },
                trailingIcon = {
                    Switch(checked = sfxOn, onCheckedChange = {
                        sfxOn = it
                        soundPlayer.sfxEnabled = it
                    })
                },
                onClick = { sfxOn = !sfxOn; soundPlayer.sfxEnabled = sfxOn },
            )
        }
    }
}