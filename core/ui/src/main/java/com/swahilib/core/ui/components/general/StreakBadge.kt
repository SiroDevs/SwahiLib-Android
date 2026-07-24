package com.swahilib.core.ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Small pill showing the user's current daily-visit streak, e.g. "🔥 5 siku mfululizo".
 * Shown on the Daily Word / Daily Proverb screens right after a visit is recorded
 * via [com.swahilib.core.data.repos.PrefsRepo.recordDailyVisit]. Renders nothing
 * for a streak of 0 or 1 so it doesn't nag first-time or one-off visitors.
 */
@Composable
fun StreakBadge(
    streakCount: Int,
    modifier: Modifier = Modifier,
) {
    if (streakCount <= 1) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = "🔥 Siku $streakCount Mfululizo",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
}
