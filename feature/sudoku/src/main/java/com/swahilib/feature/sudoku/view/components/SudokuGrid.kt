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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swahilib.sudoku.viewmodel.SudokuUiState
import kotlin.collections.indices

@Composable
fun SudokuGrid(state: SudokuUiState.Playing, onTapCell: (Int, Int) -> Unit) {
    val cellSize = (300 / state.puzzle.size.coerceAtLeast(1)).coerceIn(16, 28).dp
    Column {
        for (row in state.puzzle.grid.indices) {
            Row {
                for (col in state.puzzle.grid[row].indices) {
                    val selected = state.selectionStart == row to col
                    val letter = state.puzzle.grid[row][col]
                    val highlighted = state.highlightedLetter != null && letter == state.highlightedLetter
                    Box(
                        Modifier
                            .size(cellSize)
                            .clip(CircleShape)
                            .background(
                                when {
                                    selected -> MaterialTheme.colorScheme.primaryContainer
                                    highlighted -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable { onTapCell(row, col) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            letter.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}