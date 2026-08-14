package com.swahilib.core.ui.components.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.action.AppTopBar

/**
 * Top app bar shared by every engagement game screen.
 *
 * - [level] is shown as a "Kiwango N" tagline under the title; pass null for
 *   Quiz, which has no levels, or while on the Overview/practice-config step.
 * - [onBack] backs out through the exit-confirmation dialog; [onKill] is the
 *   small 'x' that does the exact same thing - a second, more obvious affordance
 *   for players who want out immediately.
 * - [onRefresh] triggers the restart-confirmation dialog.
 * - [soundPlayer] is optional: pass it to show the in-game music/SFX mute menu.
 *   Screens that don't play audio yet (rare) can omit it.
 */
@Composable
fun GameTopBar(
    title: String,
    level: Int?,
    previousPoints: Int,
    livePoints: Int,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onKill: () -> Unit = onBack,
    soundPlayer: GameSoundPlayer? = null,
) {
    AppTopBar(
        title = title,
        tagline = level?.let { "Kiwango $it" },
        showGoBack = true,
        onNavIconClick = onBack,
        actions = {
            GamePointsBadge(previousPoints = previousPoints, livePoints = livePoints)
            if (soundPlayer != null) {
                GameSoundMenu(soundPlayer)
            }
            IconButton(onClick = onRefresh) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Anza Upya")
            }
            IconButton(onClick = onKill) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Toka kwenye Mchezo")
            }
        },
    )
}

@Composable
private fun GameSoundMenu(soundPlayer: GameSoundPlayer) {
    var expanded by remember { mutableStateOf(false) }
    var musicOn by remember { mutableStateOf(soundPlayer.musicEnabled) }
    var sfxOn by remember { mutableStateOf(soundPlayer.sfxEnabled) }

    IconButton(onClick = { expanded = true }) {
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

@Composable
private fun GamePointsBadge(previousPoints: Int, livePoints: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "${previousPoints + livePoints}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        AnimatedVisibility(
            visible = livePoints > 0,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "(+$livePoints)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
        Spacer(modifier = Modifier.width(2.dp))
    }
}
