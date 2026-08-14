package com.swahilib.core.ui.components.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberInfiniteTransition
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin
import kotlin.random.Random

private data class FloatingShape(
    val x: Float, // 0f..1f fraction of width
    val baseSize: Float,
    val color: Color,
    val speed: Float, // seconds to cross the screen
    val delay: Float,
    val wobble: Float,
    val isBubble: Boolean,
)

private val CelebrationPalette = listOf(
    Color(0xFFFF6F91), Color(0xFFFFC75F), Color(0xFF845EC2),
    Color(0xFF00C2A8), Color(0xFF4D8AF0), Color(0xFFFF9671),
)

/**
 * Balloons-and-bubbles celebration, floating up over whatever's behind it.
 * Plays [GameSound.CHEER] once when it appears. Any tap anywhere dismisses
 * it early, same as pressing a real button on the finish screen underneath.
 */
@Composable
fun CelebrationOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    soundPlayer: GameSoundPlayer? = null,
) {
    AnimatedVisibility(visible = visible, exit = fadeOut()) {
        LaunchedEffect(Unit) {
            soundPlayer?.play(GameSound.CHEER)
        }

        val shapes = remember {
            List(18) {
                FloatingShape(
                    x = Random.nextFloat(),
                    baseSize = Random.nextFloat() * 26f + 18f,
                    color = CelebrationPalette.random(),
                    speed = Random.nextFloat() * 2.5f + 3.5f,
                    delay = Random.nextFloat() * 1.5f,
                    wobble = Random.nextFloat() * 40f + 20f,
                    isBubble = Random.nextBoolean(),
                )
            }
        }

        val infiniteTransition = rememberInfiniteTransition(label = "celebration")
        val progressSeconds by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
            label = "celebrationProgress",
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    onDismiss()
                },
        ) {
            val w = size.width
            val h = size.height
            shapes.forEach { shape ->
                val cycle = ((progressSeconds - shape.delay) / shape.speed).let { v ->
                    val m = v % 1f
                    if (m < 0f) m + 1f else m
                }
                val yFrac = 1f - cycle // rises from bottom (1) to top (0)
                val wobbleX = sin((cycle * 2f * Math.PI).toFloat()) * shape.wobble
                val cx = shape.x * w + wobbleX
                val cy = yFrac * h * 1.15f - h * 0.05f
                val alpha = (1f - cycle * 0.15f).coerceIn(0.35f, 1f)

                if (shape.isBubble) {
                    drawCircle(
                        color = shape.color.copy(alpha = alpha * 0.55f),
                        radius = shape.baseSize,
                        center = Offset(cx, cy),
                        style = Fill,
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = alpha * 0.6f),
                        radius = shape.baseSize * 0.9f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2f),
                    )
                } else {
                    // A simple balloon: an oval body plus a thin string.
                    drawOval(
                        color = shape.color.copy(alpha = alpha),
                        topLeft = Offset(cx - shape.baseSize * 0.7f, cy - shape.baseSize),
                        size = Size(shape.baseSize * 1.4f, shape.baseSize * 1.8f),
                    )
                    drawLine(
                        color = shape.color.copy(alpha = alpha * 0.6f),
                        start = Offset(cx, cy + shape.baseSize * 0.8f),
                        end = Offset(cx, cy + shape.baseSize * 1.6f),
                        strokeWidth = 2f,
                    )
                }
            }
        }
    }
}
