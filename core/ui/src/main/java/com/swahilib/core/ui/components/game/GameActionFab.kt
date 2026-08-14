package com.swahilib.core.ui.components.game

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The Extended FAB used for every game's submit/continue action, so all
 * seven games share one look instead of ad-hoc inline buttons. Disabled
 * state dims per Material defaults automatically.
 */
@Composable
fun GameActionFab(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isContinue: Boolean = false,
) {
    ExtendedFloatingActionButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.padding(bottom = 4.dp),
        containerColor = if (enabled) {
            if (isContinue) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (enabled) {
            if (isContinue) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        icon = {
            Icon(
                imageVector = if (isContinue) Icons.Default.ArrowForward else Icons.Default.Check,
                contentDescription = null,
            )
        },
        text = { Text(text) },
        elevation = FloatingActionButtonDefaults.elevation(if (enabled) 4.dp else 0.dp),
    )
}
