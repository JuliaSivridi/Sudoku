package com.example.sudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.Cell
import com.example.sudoku.ui.theme.GridInnerBorderDark
import com.example.sudoku.ui.theme.GridInnerBorderLight
import com.example.sudoku.ui.theme.LocalAppThemeColors

@Composable
fun NumberRow(
    board: List<List<Cell>>,
    selectedDigit: Int?,
    showSelection: Boolean,      // false в режиме Cell First — цифры не подсвечиваются
    onDigitSelected: (Int) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val borderColor = if (isDark) GridInnerBorderDark else GridInnerBorderLight
    val appColors = LocalAppThemeColors.current

    val digitCounts = IntArray(10)
    for (row in board) {
        for (cell in row) {
            if (cell.value in 1..9) digitCounts[cell.value]++
        }
    }

    // 3 columns × 3 rows; each cell is as wide as one 3×3 box in the grid (1/3 of total width)
    // Height = 1.5 × old square height → aspectRatio(2f): width = 2 × height
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor)
    ) {
        for (rowIdx in 0..2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        if (rowIdx > 0) {
                            drawLine(
                                color = borderColor,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
            ) {
                for (colIdx in 0..2) {
                    val digit = rowIdx * 3 + colIdx + 1
                    val isSelected = digit == selectedDigit
                    val isFull = digitCounts[digit] >= 9

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2f)
                            .background(
                                if (showSelection && isSelected && !isFull)
                                    if (isDark) appColors.cellDigitHighlightDark else appColors.cellDigitHighlightLight
                                else Color.Transparent
                            )
                            .drawBehind {
                                if (colIdx > 0) {
                                    drawLine(
                                        color = borderColor,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
                            }
                            .then(
                                if (!isFull) Modifier.clickable { onDigitSelected(digit) }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isFull) {
                            val highlighted = showSelection && isSelected
                            Text(
                                text = digit.toString(),
                                fontSize = 32.sp,
                                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (highlighted) appColors.accent
                                        else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}
