package com.swahilib.feature.social.view.screen.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.social.models.FriendChallenge
import com.swahilib.core.social.models.FriendChallengeStatus
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.feature.social.utils.activityTypeFrom
import com.swahilib.feature.social.utils.difficultyTitle
import com.swahilib.feature.social.utils.freeplayRouteFor
import com.swahilib.feature.social.utils.titleFor

fun LazyListScope.challengesTab(
    challenges: List<FriendChallenge>,
    onCreateChallenge: () -> Unit,
    onPlay: (route: String) -> Unit,
    onEnterScore: (FriendChallenge) -> Unit,
    onDecline: (String) -> Unit,
) {
    item(key = "create_challenge_button") {
        OutlinedButton(
            onClick = onCreateChallenge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text("Unda Changamoto Mpya")
        }
    }

    if (challenges.isEmpty()) {
        item(key = "challenges_empty") {
            EmptyState(
                title = "Hamna Changamoto",
                message = "Unda changamoto ili kushindana na rafiki yako.",
                messageIcon = Icons.Default.SportsEsports,
            )
        }
        return
    }

    items(challenges, key = { it.id }) { challenge ->
        ChallengeCard(
            challenge = challenge,
            onPlay = { onPlay(freeplayRouteFor(activityTypeFrom(challenge.activityType))) },
            onEnterScore = { onEnterScore(challenge) },
            onDecline = { onDecline(challenge.id) },
        )
    }
}

@Composable
private fun ChallengeCard(
    challenge: FriendChallenge,
    onPlay: () -> Unit,
    onEnterScore: () -> Unit,
    onDecline: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                "Dhidi ya ${challenge.opponent.displayName}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                "${titleFor(activityTypeFrom(challenge.activityType))} · ${difficultyTitle(challenge.difficulty)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))

            when (challenge.status) {
                FriendChallengeStatus.PENDING, FriendChallengeStatus.ACTIVE -> {
                    if (challenge.myScore == null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onPlay) { Text("Cheza") }
                            OutlinedButton(onClick = onEnterScore) { Text("Weka Alama") }
                        }
                        if (challenge.status == FriendChallengeStatus.PENDING) {
                            Spacer(Modifier.height(6.dp))
                            TextButton(onClick = onDecline) { Text("Kataa Changamoto") }
                        }
                    } else {
                        Text(
                            "Alama yako: ${challenge.myScore} · Inasubiri ${challenge.opponent.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                FriendChallengeStatus.COMPLETED -> {
                    val myScore = challenge.myScore ?: 0
                    val theirScore = challenge.opponentScore ?: 0
                    val resultText = when {
                        myScore > theirScore -> "Umeshinda! 🏆"
                        myScore < theirScore -> "Umepoteza"
                        else -> "Sare"
                    }
                    Text(
                        "Wewe: $myScore  ·  ${challenge.opponent.displayName}: $theirScore",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(resultText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }

                FriendChallengeStatus.DECLINED -> Text(
                    "Changamoto ilikataliwa", style = MaterialTheme.typography.bodySmall,
                )

                FriendChallengeStatus.EXPIRED -> Text(
                    "Muda wa changamoto umeisha", style = MaterialTheme.typography.bodySmall,
                )

                FriendChallengeStatus.UNKNOWN -> Text(
                    "Hali haijulikani", style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
