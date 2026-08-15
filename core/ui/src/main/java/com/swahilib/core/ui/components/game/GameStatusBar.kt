package com.swahilib.core.ui.components.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * The in-game status row shown at the top of every Playing screen: a
 * countdown badge (M:SS), a points badge, and a pause/resume toggle - each
 * its own colored rounded chip - with a thin progress line underneath
 * spanning the full width to show time remaining at a glance.
 */
@Composable
fun GameStatusBar(
    remainingSeconds: Int,
    totalSeconds: Int,
    previousPoints: Int,
    livePoints: Int,
    paused: Boolean,
    onTogglePause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = if (totalSeconds <= 0) 0f else (remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "gameStatusBarFraction")
    val urgent = !paused && remainingSeconds in 1..5

    val timerColor = when {
        fraction > 0.4f -> MaterialTheme.colorScheme.primary
        fraction > 0.15f -> Color(0xFFF9A825)
        else -> MaterialTheme.colorScheme.error
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gameStatusBarPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (urgent) 1.08f else 1f,
        animationSpec = infiniteRepeatable(tween(420), repeatMode = RepeatMode.Reverse),
        label = "gameStatusBarPulseScale",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatusChip(
                containerColor = timerColor.copy(alpha = 0.15f),
                contentColor = timerColor,
                modifier = Modifier.scale(if (urgent) pulse else 1f),
            ) {
                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.size(4.dp))
                Text(formatMmSs(remainingSeconds), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }

            StatusChip(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) {
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

            StatusChip(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                paddingHorizontal = 6.dp,
            ) {
                IconButton(onClick = onTogglePause, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (paused) "Endelea" else "Simamisha",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = timerColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun StatusChip(
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    paddingHorizontal: androidx.compose.ui.unit.Dp = 12.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = paddingHorizontal, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides contentColor,
                content = { content() },
            )
        }
    }
}

private fun formatMmSs(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return "%d:%02d".format(minutes, seconds)
}
