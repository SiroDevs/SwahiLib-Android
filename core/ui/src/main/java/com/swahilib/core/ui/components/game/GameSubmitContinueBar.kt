package com.swahilib.core.ui.components.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameSubmitContinueBar(
    onSubmit: (() -> Unit)?,
    submitEnabled: Boolean,
    onContinue: (() -> Unit)?,
    continueEnabled: Boolean,
    modifier: Modifier = Modifier,
    submitLabel: String = "Wasilisha",
    continueLabel: String = "Endelea",
) {
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        if (onSubmit != null) {
            GameActionFab(
                text = submitLabel,
                onClick = onSubmit,
                enabled = submitEnabled,
                isContinue = false,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }
        if (onContinue != null) {
            GameActionFab(
                text = continueLabel,
                onClick = onContinue,
                enabled = continueEnabled,
                isContinue = true,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}
