package com.swahilib.feature.social.view.screen.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.core.engagement.catalog.AchievementCatalog
import com.swahilib.core.social.models.AchievementFeedItem
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.progress.achievementIconFor

fun LazyListScope.achievementFeedTab(items: List<AchievementFeedItem>) {
    if (items.isEmpty()) {
        item(key = "achievement_feed_empty") {
            EmptyState(
                title = "Hamna Mafanikio Bado",
                message = "Beji za marafiki zako zitaonekana hapa zinapofunguliwa.",
                messageIcon = Icons.Default.EmojiEvents,
            )
        }
        return
    }

    items(items, key = { "${it.friendDisplayName}_${it.achievementId}_${it.unlockedAt}" }) { feedItem ->
        AchievementFeedRow(feedItem)
    }
}

@Composable
private fun AchievementFeedRow(item: AchievementFeedItem) {
    val achievement = AchievementCatalog.byId(item.achievementId)
    ListItem(
        leadingContent = {
            Box(modifier = Modifier.size(28.dp)) {
                Icon(
                    achievementIconFor(achievement?.iconKey ?: "default"),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        headlineContent = { Text("${item.friendDisplayName} amefungua ${achievement?.title ?: item.achievementId}") },
        supportingContent = { Text(formatUnlockedAt(item.unlockedAt)) },
    )
}

private fun formatUnlockedAt(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return iso.substringBefore("T")
}
