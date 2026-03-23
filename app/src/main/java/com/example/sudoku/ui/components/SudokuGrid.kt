package com.example.sudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.Cell
import com.example.sudoku.model.GameState
import com.example.sudoku.ui.theme.CellDigitHighlight
import com.example.sudoku.ui.theme.CellHighlightDark
import com.example.sudoku.ui.theme.CellHighlightLight
import com.example.sudoku.ui.theme.CellSelectedDark
import com.example.sudoku.ui.theme.CellSelectedLight
import com.example.sudoku.ui.theme.ConflictColorDark
import com.example.sudoku.ui.theme.ConflictColorLight
import com.example.sudoku.ui.theme.GivenNumberDark
import com.example.sudoku.ui.theme.GivenNumberLight
import com.example.sudoku.ui.theme.GridInnerBorderDark
import com.example.sudoku.ui.theme.GridInnerBorderLight
import com.example.sudoku.ui.theme.GridOuterBorderDark
import com.example.sudoku.ui.theme.GridOuterBorderLight
import com.example.sudoku.ui.theme.NoteNumberDark
import com.example.sudoku.ui.theme.NoteNumberLight
import com.example.sudoku.ui.theme.UserNumberDark
import com.example.sudoku.ui.theme.UserNumberLight

@Composable
fun SudokuGrid(
    state: GameState,
    onCellTap: (Int, Int) -> Unit,
    isSolverMode: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    val outerBorder = if (isDark) GridOuterBorderDark else GridOuterBorderLight
    val innerBorder = if (isDark) GridInnerBorderDark else GridInnerBorderLight

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .drawWithContent {
                drawContent()

                val cellW = size.width / 9f
                val cellH = size.height / 9f
                val thin = 1.dp.toPx()
                val thick = 2.dp.toPx()

                // Внутренние линии — рисуем поверх ячеек
                for (i in 1..8) {
                    val isBoxBoundary = i % 3 == 0
                    val color = if (isBoxBoundary) outerBorder else innerBorder
                    val stroke = if (isBoxBoundary) thick else thin

                    drawLine(
                        color = color,
                        start = Offset(0f, i * cellH),
                        end = Offset(size.width, i * cellH),
                        strokeWidth = stroke
                    )
                    drawLine(
                        color = color,
                        start = Offset(i * cellW, 0f),
                        end = Offset(i * cellW, size.height),
                        strokeWidth = stroke
                    )
                }

                // Внешняя рамка — всегда поверх всего
                val half = thick / 2f
                drawLine(outerBorder, Offset(half, 0f), Offset(half, size.height), thick)
                drawLine(outerBorder, Offset(size.width - half, 0f), Offset(size.width - half, size.height), thick)
                drawLine(outerBorder, Offset(0f, half), Offset(size.width, half), thick)
                drawLine(outerBorder, Offset(0f, size.height - half), Offset(size.width, size.height - half), thick)
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0..8) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    for (col in 0..8) {
                        val cell = state.board[row][col]
                        val isSelected = state.selectedCell == Pair(row, col)
                        val isHighlighted = isInHighlightArea(state, row, col)
                        val isDigitMatch = state.selectedDigit != null &&
                                cell.value != 0 &&
                                cell.value == state.selectedDigit

                        val bgColor = when {
                            isSelected -> if (isDark) CellSelectedDark else CellSelectedLight
                            isDigitMatch -> CellDigitHighlight
                            isHighlighted -> if (isDark) CellHighlightDark else CellHighlightLight
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(bgColor)
                                .clickable { onCellTap(row, col) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.notes.isNotEmpty() && cell.value == 0) {
                                NotesGrid(notes = cell.notes, isDark = isDark)
                            } else if (cell.value != 0) {
                                val textColor = when {
                                    isSolverMode -> if (isDark) UserNumberDark else UserNumberLight
                                    cell.isGiven -> if (isDark) GivenNumberDark else GivenNumberLight
                                    !isSolverMode && hasConflict(state.board, row, col) ->
                                        if (isDark) ConflictColorDark else ConflictColorLight
                                    else -> if (isDark) UserNumberDark else UserNumberLight
                                }
                                Text(
                                    text = cell.value.toString(),
                                    fontSize = 24.sp,
                                    fontWeight = if (!isSolverMode && cell.isGiven) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Заметки: фиксированная мини-сетка 3×3
@Composable
private fun NotesGrid(notes: Set<Int>, isDark: Boolean) {
    val noteColor = if (isDark) NoteNumberDark else NoteNumberLight
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
    ) {
        for (noteRow in 0..2) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                for (noteCol in 0..2) {
                    val digit = noteRow * 3 + noteCol + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (digit in notes) {
                            Text(
                                text = digit.toString(),
                                fontSize = 8.sp,
                                color = noteColor,
                                textAlign = TextAlign.Center,
                                lineHeight = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun isInHighlightArea(state: GameState, row: Int, col: Int): Boolean {
    val sel = state.selectedCell ?: return false
    val (selRow, selCol) = sel
    return row == selRow || col == selCol
}

private fun hasConflict(board: List<List<Cell>>, row: Int, col: Int): Boolean {
    val cell = board[row][col]
    if (cell.isGiven || cell.value == 0) return false
    val value = cell.value
    // Check row
    for (c in 0..8) {
        if (c != col && board[row][c].value == value) return true
    }
    // Check column
    for (r in 0..8) {
        if (r != row && board[r][col].value == value) return true
    }
    // Check 3x3 box
    val boxRow = (row / 3) * 3
    val boxCol = (col / 3) * 3
    for (r in boxRow until boxRow + 3) {
        for (c in boxCol until boxCol + 3) {
            if ((r != row || c != col) && board[r][c].value == value) return true
        }
    }
    return false
}
