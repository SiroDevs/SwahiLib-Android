package com.swahilib.core.ui.components.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class GameLevelUiModel(
    val level: Int,
    val bannerPoints: Int,
    val unlocked: Boolean,
    val isCurrent: Boolean,
)

/**
 * A carousel-styled (peeking side cards) row of level cards. It intentionally
 * does not respond to swipe gestures - [levels] are laid out once and the
 * currently-eligible level (the highest unlocked one) is auto-centered on
 * appearance so it reads as "in front". Selecting a level is a tap, not a
 * scroll gesture.
 */
@Composable
fun LevelCarousel(
    levels: List<GameLevelUiModel>,
    onLevelTap: (GameLevelUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardWidth = 132.dp
    val listState = rememberLazyListState()
    val currentIndex = remember(levels) {
        levels.indexOfFirst { it.isCurrent }.coerceAtLeast(0)
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val available: Dp = maxWidth - cardWidth
        val horizontalPadding: Dp = if (available > 0.dp) available / 2 else 0.dp

        LaunchedEffect(currentIndex) {
            listState.animateScrollToItem(currentIndex)
        }

        LazyRow(
            state = listState,
            userScrollEnabled = false,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(levels, key = { it.level }) { level ->
                LevelCard(
                    model = level,
                    cardWidth = cardWidth,
                    onTap = { onLevelTap(level) },
                )
            }
        }
    }
}

@Composable
private fun LevelCard(model: GameLevelUiModel, cardWidth: Dp, onTap: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (model.isCurrent) 1f else 0.88f,
        animationSpec = tween(220),
        label = "levelCardScale",
    )
    val containerColor = when {
        !model.unlocked -> MaterialTheme.colorScheme.surfaceVariant
        model.isCurrent -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = when {
        !model.unlocked -> MaterialTheme.colorScheme.onSurfaceVariant
        model.isCurrent -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = Modifier
            .size(width = cardWidth, height = 168.dp)
            .scale(scale)
            .combinedClickable(onClick = onTap),
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        border = if (model.isCurrent) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (model.isCurrent) 6.dp else 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Kiwango",
                style = MaterialTheme.typography.labelMedium,
            )
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(contentColor.copy(alpha = 0.12f), shape = MaterialTheme.shapes.large),
                contentAlignment = Alignment.Center,
            ) {
                if (model.unlocked) {
                    Text(text = "${model.level}", style = MaterialTheme.typography.headlineMedium)
                } else {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Bado haijafunguliwa")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${model.bannerPoints}", style = MaterialTheme.typography.labelMedium)
            }
            if (model.unlocked) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = contentColor,
                    )
                    Text(text = "Cheza", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
