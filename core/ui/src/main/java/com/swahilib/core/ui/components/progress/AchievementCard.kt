package com.swahilib.core.ui.components.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swahilib.core.engagement.model.Achievement

@Composable
fun AchievementCard(achievement: Achievement, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().aspectRatio(0.85f),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
        ),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .alpha(if (achievement.unlocked) 1f else 0.45f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                achievementIconFor(achievement.iconKey),
                contentDescription = null,
                tint = if (achievement.unlocked) {
                    MaterialTheme.colorScheme.onTertiaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                achievement.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Text(
                achievement.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
        }
    }
}
