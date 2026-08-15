package com.swahilib.feature.crossword.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swahilib.core.games.model.CrosswordDirection
import com.swahilib.core.games.model.CrosswordEntry
import com.swahilib.core.ui.components.game.GameActionFab
import com.swahilib.core.ui.components.game.GameStatusBar
import com.swahilib.feature.crossword.utils.CrosswordUiState
import kotlin.collections.orEmpty
import kotlin.collections.sortedBy
import kotlin.text.orEmpty

@Composable
fun PlayingContent(
    state: CrosswordUiState.Playing,
    onAnswerChange: (String, String) -> Unit,
    onFocus: (String) -> Unit,
    onFinish: () -> Unit,
    onTogglePause: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            GameStatusBar(
                remainingSeconds = state.secondsRemaining,
                totalSeconds = state.secondsTotal,
                previousPoints = state.previousPoints,
                livePoints = 0,
                paused = state.paused,
                onTogglePause = onTogglePause,
            )
            if (state.practice) {
                Spacer(Modifier.height(4.dp))
                Text("MAZOEZI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                CrosswordGrid(state.puzzle, state.answers, state.focusedEntryId)
            }
        }

        // Clues float as a scrollable, semi-transparent panel over the lower part of the grid.
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.42f),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.93f),
            tonalElevation = 4.dp,
        ) {
            Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small),
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    val byDirection = state.puzzle.entries.groupBy { it.direction }
                    byDirection[CrosswordDirection.ACROSS]?.sortedBy { it.number }?.let { entries ->
                        item { ClueSectionHeader("Mlalo (Across)") }
                        items(entries) { entry -> ClueRow(entry, state.answers[entry.id].orEmpty(), state.focusedEntryId == entry.id, state.easyMode, !state.paused, onFocus, onAnswerChange) }
                    }
                    byDirection[CrosswordDirection.DOWN]?.sortedBy { it.number }?.let { entries ->
                        item { ClueSectionHeader("Wima (Down)") }
                        items(entries) { entry -> ClueRow(entry, state.answers[entry.id].orEmpty(), state.focusedEntryId == entry.id, state.easyMode, !state.paused, onFocus, onAnswerChange) }
                    }
                    val diagonals = (byDirection[CrosswordDirection.DIAGONAL_DOWN_RIGHT].orEmpty() + byDirection[CrosswordDirection.DIAGONAL_DOWN_LEFT].orEmpty())
                        .sortedBy { it.number }
                    if (diagonals.isNotEmpty()) {
                        item { ClueSectionHeader("Mshazari (Diagonal)") }
                        items(diagonals) { entry -> ClueRow(entry, state.answers[entry.id].orEmpty(), state.focusedEntryId == entry.id, state.easyMode, !state.paused, onFocus, onAnswerChange) }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    GameActionFab(text = "Maliza", onClick = onFinish, enabled = !state.paused)
                }
            }
        }

        AnimatedVisibility(visible = state.paused, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Mchezo Umesimamishwa",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onTogglePause) { Text("Endelea na Mchezo") }
                }
            }
        }
    }
}

@Composable
private fun ClueSectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
}

@Composable
private fun ClueRow(
    entry: CrosswordEntry,
    typed: String,
    focused: Boolean,
    easyMode: Boolean,
    enabled: Boolean,
    onFocus: (String) -> Unit,
    onAnswerChange: (String, String) -> Unit,
) {
    OutlinedTextField(
        value = typed,
        onValueChange = { if (!easyMode && enabled) onAnswerChange(entry.id, it) },
        label = { Text("${entry.number}. ${entry.clue}") },
        singleLine = true,
        readOnly = easyMode || !enabled,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused && enabled) onFocus(entry.id) }
            .let { if (easyMode) it.clickable(enabled = enabled) { onFocus(entry.id) } else it }
            .let { if (focused) it.border(1.5.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small) else it },
    )
}
