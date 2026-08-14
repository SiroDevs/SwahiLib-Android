package com.swahilib.core.ui.components.game

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * The per-step countdown, redesigned as a big ring badge rather than a thin
 * bar so it reads at a glance during play. Turns amber under 40% remaining,
 * red with a pulse in the last 5 seconds.
 *
 * [size] defaults to a dominant 84dp - large enough to anchor the top of the
 * play screen. Pass a smaller value for tight layouts (e.g. inline headers).
 */
@Composable
fun StepTimerBar(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 84.dp,
) {
    val fraction = if (totalSeconds <= 0) 0f else (remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "timerFraction")
    val urgent = remainingSeconds in 1..5

    val color = when {
        fraction > 0.4f -> MaterialTheme.colorScheme.primary
        fraction > 0.15f -> Color(0xFFF9A825)
        else -> MaterialTheme.colorScheme.error
    }
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (urgent) 1.1f else 1f,
        animationSpec = infiniteRepeatable(tween(420), repeatMode = RepeatMode.Reverse),
        label = "timerPulseScale",
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(if (urgent) pulse else 1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = size.toPx() * 0.11f
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedFraction,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
            )
        }
        Text(
            text = "$remainingSeconds",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = color,
        )
    }
}
