package com.swahilib.feature.social.view.screen.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.social.models.Friend
import com.swahilib.core.social.models.FriendshipStatus
import com.swahilib.core.social.models.SocialProfile
import com.swahilib.core.ui.components.indicators.EmptyState

fun LazyListScope.friendsTab(
    profile: SocialProfile?,
    friends: List<Friend>,
    clipboard: ClipboardManager,
    onAddFriend: () -> Unit,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    if (profile != null) {
        item(key = "friend_code_card") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            "Msimbo Wako wa Rafiki",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            profile.friendCode,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    IconButton(onClick = {
                        clipboard.setText(AnnotatedString(profile.friendCode))
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Nakili Msimbo")
                    }
                }
            }
        }
    }

    item(key = "add_friend_button") {
        OutlinedButton(
            onClick = onAddFriend,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Ongeza Rafiki kwa Msimbo")
        }
    }

    val received = friends.filter { it.status == FriendshipStatus.PENDING && !it.requestedByMe }
    val sent = friends.filter { it.status == FriendshipStatus.PENDING && it.requestedByMe }
    val accepted = friends.filter { it.status == FriendshipStatus.ACCEPTED }

    if (received.isNotEmpty()) {
        item(key = "received_header") {
            Text(
                "Maombi Yaliyokujia",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        items(received, key = { it.friendshipId }) { friend ->
            ListItem(
                headlineContent = { Text(friend.profile.displayName) },
                supportingContent = { Text("Ngazi ${friend.profile.level}") },
                trailingContent = {
                    Row {
                        TextButton(onClick = { onAccept(friend.friendshipId) }) { Text("Kubali") }
                        TextButton(onClick = { onDecline(friend.friendshipId) }) { Text("Kataa") }
                    }
                },
            )
        }
    }

    if (sent.isNotEmpty()) {
        item(key = "sent_header") {
            Text(
                "Maombi Uliyotuma",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        items(sent, key = { it.friendshipId }) { friend ->
            ListItem(
                headlineContent = { Text(friend.profile.displayName) },
                supportingContent = { Text("Inasubiri jibu") },
            )
        }
    }

    item(key = "friends_header") {
        Text(
            "Marafiki",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }

    if (accepted.isEmpty()) {
        item(key = "friends_empty") {
            EmptyState(
                title = "Hamna Marafiki Bado",
                message = "Shiriki msimbo wako au ongeza rafiki kwa msimbo wao.",
                messageIcon = Icons.Default.Groups,
            )
        }
    } else {
        items(accepted, key = { it.friendshipId }) { friend ->
            ListItem(
                headlineContent = { Text(friend.profile.displayName) },
                supportingContent = { Text("Ngazi ${friend.profile.level} · ${friend.profile.totalXp} XP") },
                trailingContent = {
                    IconButton(onClick = { onRemove(friend.friendshipId) }) {
                        Icon(Icons.Default.PersonRemove, contentDescription = "Ondoa Rafiki")
                    }
                },
            )
        }
    }
}
