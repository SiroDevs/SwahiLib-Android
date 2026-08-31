package com.swahilib.feature.sudoku.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.ui.components.game.AndroidPauseOverlay
import com.swahilib.core.ui.components.game.GameActionFab
import com.swahilib.feature.sudoku.utils.SudokuUiState

@Composable
fun PlayingSudoku(
    state: SudokuUiState.Playing,
    onTapCell: (Int, Int) -> Unit,
    onGiveUp: () -> Unit,
    onTogglePause: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            GameStatusRow(
                remainingSeconds = state.secondsRemaining,
                previousPoints = state.previousPoints,
                livePoints = state.livePoints,
                paused = state.paused,
                onTogglePause = onTogglePause,
            )
            Spacer(Modifier.height(8.dp))
            if (state.practice) {
                Text("MAZOEZI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                "Gusa herufi ya kwanza, kisha herufi ya mwisho, ya kila neno.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.lastMissed) {
                Text("Hakuna neno hapo - jaribu tena.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                SudokuGrid(state, onTapCell)
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.36f),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.93f),
            tonalElevation = 4.dp,
        ) {
            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small),
                )
                Spacer(Modifier.height(8.dp))
                Text("Maneno ya Kutafuta", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.words, key = { it.word }) { word -> WordListRow(word) }
                }
                Spacer(Modifier.height(6.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GameActionFab(text = "Maliza / Toka Mchezoni", onClick = onGiveUp, enabled = !state.paused)
                }
            }
        }

        AndroidPauseOverlay(visible = state.paused, onResume = onTogglePause)
    }
}

@Composable
private fun GameStatusRow(
    remainingSeconds: Int,
    previousPoints: Int,
    livePoints: Int,
    paused: Boolean,
    onTogglePause: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatusChip(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.primary) {
            Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(4.dp))
            Text(formatMmSs(remainingSeconds), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }
        StatusChip(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer) {
            Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(4.dp))
            Text("${previousPoints + livePoints}", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            if (livePoints > 0) {
                Text(
                    " (+$livePoints)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                )
            }
        }
        Spacer(Modifier.weight(1f))
        StatusChip(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer, paddingHorizontal = 6.dp) {
            IconButton(onClick = onTogglePause, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (paused) "Endelea" else "Simamisha",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    containerColor: Color,
    contentColor: Color,
    paddingHorizontal: androidx.compose.ui.unit.Dp = 12.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = paddingHorizontal, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CompositionLocalProvider(LocalContentColor provides contentColor, content = { content() })
        }
    }
}

private fun formatMmSs(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun WordListRow(word: PlacedWord) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (word.found) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    word.word,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (word.found) TextDecoration.LineThrough else null,
                    ),
                )
                Text(word.clue, style = MaterialTheme.typography.bodySmall)
            }
            if (word.found) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}
