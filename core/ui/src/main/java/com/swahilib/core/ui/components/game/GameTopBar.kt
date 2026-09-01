package com.swahilib.core.ui.components.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar(
    title: String,
    level: Int?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onKill: () -> Unit = onBack,
    previousPoints: Int? = null,
    livePoints: Int = 0,
    isPractice: Boolean = false,
) {
    val titleContent: @Composable () -> Unit = {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row() {
                Text(
                    text = "Kiwango $level",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(10.dp))
                if (isPractice) {
                    Text("MAZOEZI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }

    val navIcon: @Composable () -> Unit = {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Rudi Nyuma",
            )
        }
    }

    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface,
    )

    TopAppBar(
        title = titleContent,
        navigationIcon = navIcon,
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = colors,
        actions = {
            if (previousPoints != null) {
                StatusChip(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    Icon(
                        Icons.Default.Stars,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        "${previousPoints?.plus(livePoints)}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    if (livePoints > 0) {
                        Text(
                            " (+$livePoints)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                        )
                    }
                }
            }
            IconButton(onClick = onRefresh) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Anza Upya")
            }
            IconButton(onClick = onKill) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Toka Mchezoni")
            }
        },
    )
}
