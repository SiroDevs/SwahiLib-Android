package com.swahilib.core.ui.components.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.engagement.model.Challenge
import com.swahilib.core.engagement.model.ChallengeActivity
import com.swahilib.core.engagement.model.ChallengeScope

@Composable
fun ChallengeCard(
    challenge: Challenge,
    modifier: Modifier = Modifier,
    onStartActivity: (ChallengeActivity) -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    challenge.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    labelFor(challenge.scope),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            ProgressBar(
                fraction = challenge.progress,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${challenge.activities.count { it.completed }}/${challenge.activities.size} zimekamilika • " +
                    "+${challenge.xpReward} XP • 🪙 ${challenge.coinReward}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))

            challenge.activities.forEach { activity ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            (if (activity.completed) "✅ " else "◻️ ") + activity.title,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            "${activity.estimatedSeconds}s • +${activity.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (!activity.completed && !challenge.completed) {
                        FilledTonalButton(onClick = { onStartActivity(activity) }) {
                            Text("Anza")
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

private fun labelFor(scope: ChallengeScope): String = when (scope) {
    ChallengeScope.DAILY -> "SIKU"
    ChallengeScope.WEEKLY -> "WIKI"
    ChallengeScope.MONTHLY -> "MWEZI"
    ChallengeScope.PRACTICE -> "MAZOEZI"
    ChallengeScope.SEASONAL -> "TUKIO MAALUM"
}
