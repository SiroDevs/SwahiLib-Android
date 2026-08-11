package com.swahilib.core.ui.components.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.action.AppTopBar

/**
 * Top app bar shared by every engagement game screen.
 *
 * - [level] is shown as a "Kiwango N" tagline under the title; pass null for
 *   Quiz, which has no levels.
 * - [previousPoints] is the points banked for this game before this session
 *   ("Points in the game (gotten previously)").
 * - [livePoints] is the running total earned during the current session
 *   ("Points in the game realtime as player plays").
 * - Back always routes through [onBack] (never pops directly) so the caller
 *   can show the exit-confirmation dialog. Refresh routes through
 *   [onRefresh] so the caller can show the restart-confirmation dialog.
 */
@Composable
fun GameTopBar(
    title: String,
    level: Int?,
    previousPoints: Int,
    livePoints: Int,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
) {
    AppTopBar(
        title = title,
        tagline = level?.let { "Kiwango $it" },
        showGoBack = true,
        onNavIconClick = onBack,
        actions = {
            GamePointsBadge(previousPoints = previousPoints, livePoints = livePoints)
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Anza Upya",
                )
            }
        },
    )
}

@Composable
private fun GamePointsBadge(previousPoints: Int, livePoints: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "${previousPoints + livePoints}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        AnimatedVisibility(
            visible = livePoints > 0,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "(+$livePoints)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
    }
}
