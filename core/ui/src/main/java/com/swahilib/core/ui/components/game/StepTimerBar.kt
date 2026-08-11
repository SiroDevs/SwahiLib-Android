package com.swahilib.core.ui.components.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StepTimerBar(remainingSeconds: Int, totalSeconds: Int, modifier: Modifier = Modifier) {
    val fraction = if (totalSeconds <= 0) 0f else (remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "timerFraction")
    val urgent = remainingSeconds in 1..5

    val color = when {
        fraction > 0.4f -> MaterialTheme.colorScheme.primary
        fraction > 0.15f -> Color(0xFFF9A825)
        else -> MaterialTheme.colorScheme.error
    }

    val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (urgent) 1.12f else 1f,
        animationSpec = infiniteRepeatable(tween(420), repeatMode = RepeatMode.Reverse),
        label = "timerPulseScale",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(18.dp)
                .scale(if (urgent) pulse else 1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { animatedFraction },
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${remainingSeconds}s",
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
    }
}
