package com.swahilib.feature.social.view.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.social.dto.LeaderboardEntry
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.feature.social.viewmodel.LeaderboardScope

fun LazyListScope.leaderboardTab(
    scope: LeaderboardScope,
    entries: List<LeaderboardEntry>,
    onScopeChange: (LeaderboardScope) -> Unit,
) {
    item(key = "leaderboard_scope_chips") {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = scope == LeaderboardScope.GLOBAL,
                onClick = { onScopeChange(LeaderboardScope.GLOBAL) },
                label = { Text("Kimataifa") },
            )
            FilterChip(
                selected = scope == LeaderboardScope.FRIENDS,
                onClick = { onScopeChange(LeaderboardScope.FRIENDS) },
                label = { Text("Marafiki") },
            )
        }
    }

    if (entries.isEmpty()) {
        item(key = "leaderboard_empty") {
            EmptyState(
                title = "Hamna Bado",
                message = if (scope == LeaderboardScope.FRIENDS)
                    "Ongeza marafiki ili muonane kwenye ubao huu." else "Ubao utaonekana hapa hivi karibuni.",
                messageIcon = Icons.Default.EmojiEvents,
            )
        }
        return
    }

    items(entries, key = { it.userId }) { entry ->
        LeaderboardRow(entry)
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = if (entry.isCurrentUser)
                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
        ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("${entry.rank}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        headlineContent = { Text(entry.displayName) },
        supportingContent = { Text("Ngazi ${entry.level}") },
        trailingContent = {
            Text(
                "${entry.totalXp} XP",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        },
    )
}
