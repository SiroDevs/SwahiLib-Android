package com.swahilib.feature.crossword.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swahilib.core.games.model.CrosswordEntry
import com.swahilib.core.games.model.CrosswordPuzzle

@Composable
fun CrosswordGrid(puzzle: CrosswordPuzzle, answers: Map<String, String>, focusedEntryId: String?) {
    val cellSize = (280 / puzzle.cols.coerceAtLeast(1)).coerceIn(20, 34).dp

    Column(
        Modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        for (row in 0 until puzzle.rows) {
            Row {
                for (col in 0 until puzzle.cols) {
                    val cell = puzzle.cells[row to col]
                    if (cell == null) {
                        Box(Modifier.size(cellSize).background(MaterialTheme.colorScheme.surfaceVariant))
                    } else {
                        val coveringOffsets = puzzle.entries.mapNotNull { entry -> entry.covers(row, col)?.let { entry to it } }
                        val focusedHere = focusedEntryId != null && coveringOffsets.any { it.first.id == focusedEntryId }
                        val typedChar = coveringOffsets.firstNotNullOfOrNull { (entry, offset) -> answers[entry.id]?.getOrNull(offset) }
                        val background = if (focusedHere) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        Box(
                            Modifier
                                .size(cellSize)
                                .background(background)
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            cell.number?.let {
                                Text(it.toString(), fontSize = 8.sp, modifier = Modifier.padding(1.dp))
                            }
                            Text(
                                (typedChar ?: ' ').toString().uppercase(),
                                modifier = Modifier.fillMaxSize(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun CrosswordEntry.covers(row: Int, col: Int): Int? {
    for (i in answer.indices) {
        if (this.row + i * direction.dRow == row && this.col + i * direction.dCol == col) return i
    }
    return null
}
