package com.example.sudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import com.example.sudoku.ui.theme.Orange

@Composable
fun NumberRow(
    board: List<List<Cell>>,
    selectedDigit: Int?,
    onDigitSelected: (Int) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val borderColor = if (isDark) GridInnerBorderDark else GridInnerBorderLight

    val digitCounts = IntArray(10)
    for (row in board) {
        for (cell in row) {
            if (cell.value in 1..9) digitCounts[cell.value]++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (digit in 1..9) {
                val isSelected = digit == selectedDigit
                val isFull = digitCounts[digit] >= 9

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(
                            if (isSelected && !isFull) Orange.copy(alpha = 0.15f)
                            else Color.Transparent
                        )
                        .drawBehind {
                            if (digit > 1) {
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
                        Text(
                            text = digit.toString(),
                            fontSize = 22.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) Orange
                                    else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
