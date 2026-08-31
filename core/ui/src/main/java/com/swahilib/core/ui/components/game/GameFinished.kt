package com.swahilib.core.ui.components.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.ui.components.progress.AchievementUnlockBanner

@Composable
fun GameFinished(
    practice: Boolean,
    headline: String,
    statLines: List<String>,
    xpEarned: Int,
    level: Int?,
    pointsEarned: Int,
    unlockedAchievements: List<Achievement>,
    soundPlayer: GameSoundPlayer,
    onPlayAgain: () -> Unit,
    onDone: () -> Unit,
    reviewItems: LazyListScope.() -> Unit,
    modifier: Modifier = Modifier,
    reviewSectionTitle: String? = "Majibu",
    extraContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    var celebrating by remember { mutableStateOf(true) }

    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (practice) {
                Text("MAZOEZI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(4.dp))
            }
            Text(headline, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(12.dp))
            statLines.forEach { line -> Text(line, style = MaterialTheme.typography.titleMedium) }
            if (!practice) {
                Text("+$xpEarned XP", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                if (level != null) {
                    Text(
                        "+$pointsEarned sign - Kiwango $level",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            extraContent?.invoke(this)
            Spacer(Modifier.height(16.dp))
            AchievementUnlockBanner(unlockedAchievements, modifier = Modifier.padding(bottom = 8.dp))

            if (reviewSectionTitle != null) {
                Text(
                    reviewSectionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp),
                )
            }
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reviewItems()
            }

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (level != null) {
                    OutlinedButton(onClick = { celebrating = false; onPlayAgain() }, modifier = Modifier.weight(1f)) { Text("Viwango") }
                }
                GameActionFab(text = "Sawa", onClick = { celebrating = false; onDone() }, modifier = Modifier.weight(1f), isContinue = true)
            }
        }
        CelebrationOverlay(visible = celebrating, onDismiss = { celebrating = false }, soundPlayer = soundPlayer)
    }
}

@Composable
fun GameReviewRow(
    correct: Boolean,
    primaryText: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    tertiaryText: String? = null,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (correct) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = if (correct) Icons.Default.CheckCircle else Icons.Default.Close, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(primaryText, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                if (!secondaryText.isNullOrBlank()) Text(secondaryText, style = MaterialTheme.typography.bodySmall)
                if (!tertiaryText.isNullOrBlank()) Text(tertiaryText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
