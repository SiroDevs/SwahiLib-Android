package com.swahilib.feature.sudoku.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.feature.sudoku.utils.SudokuUiState
import kotlin.collections.indices

/** Every (row, col) this word's path passes through, regardless of direction (incl. diagonals). */
private fun PlacedWord.coveredCells(): List<Pair<Int, Int>> =
    word.indices.map { i -> (row + i * direction.dRow) to (col + i * direction.dCol) }

@Composable
fun SudokuGrid(state: SudokuUiState.Playing, onTapCell: (Int, Int) -> Unit) {
    val cellSize = (300 / state.puzzle.size.coerceAtLeast(1)).coerceIn(16, 28).dp

    // Cells belonging to an already-found word - shaded permanently, same spirit as
    // crossing a found word off the list below.
    val foundCells = remember(state.words) {
        state.words.filter { it.found }.flatMap { it.coveredCells() }.toSet()
    }

    Column {
        for (row in state.puzzle.grid.indices) {
            Row {
                for (col in state.puzzle.grid[row].indices) {
                    val selected = state.selectionStart == row to col
                    val letter = state.puzzle.grid[row][col]
                    val highlighted = state.highlightedLetter != null && letter == state.highlightedLetter
                    val found = (row to col) in foundCells
                    Box(
                        Modifier
                            .size(cellSize)
                            .clip(CircleShape)
                            .background(
                                when {
                                    found -> MaterialTheme.colorScheme.tertiaryContainer
                                    selected -> MaterialTheme.colorScheme.primaryContainer
                                    highlighted -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable(enabled = !found && !state.paused) { onTapCell(row, col) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            letter.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = if (found) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}