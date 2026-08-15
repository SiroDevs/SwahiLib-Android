package com.swahilib.core.ui.components.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The bottom action row shared by the games that answer one step at a time
 * (Quiz, Word Builder, Sentence Builder, Spelling): "Wasilisha" (submit) on
 * the left locks in the current answer, "Endelea" (continue) on the right
 * moves to the next step once an answer has been recorded. Either side can
 * be omitted (pass null) for games with only one action, e.g. Hangman only
 * ever needs the continue side once a round ends.
 */
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
