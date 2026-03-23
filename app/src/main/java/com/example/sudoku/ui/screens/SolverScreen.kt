package com.example.sudoku.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.Cell
import com.example.sudoku.model.GameState
import com.example.sudoku.model.Difficulty
import com.example.sudoku.ui.components.NumberRow
import com.example.sudoku.ui.components.SudokuGrid
import com.example.sudoku.ui.theme.ConflictColorDark
import com.example.sudoku.ui.theme.ConflictColorLight
import com.example.sudoku.ui.theme.Orange
import com.example.sudoku.viewmodel.SolverViewModel

@Composable
fun SolverScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isDark = isSystemInDarkTheme()

    // Wrap SolverState into a GameState-compatible form for SudokuGrid reuse
    val gameState = GameState(
        board = state.board,
        solution = List(9) { List(9) { 0 } },
        difficulty = Difficulty.EASY,
        selectedCell = null,
        selectedDigit = state.selectedDigit,
        isComplete = false,
        undoStack = emptyList()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top bar: back arrow + title
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Solver",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sudoku grid — solver mode: all digits treated as user digits, no conflict highlighting
        SudokuGrid(
            state = gameState,
            onCellTap = { row, col -> viewModel.onCellTap(row, col) },
            isSolverMode = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons: Undo and Clear
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SolverActionButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "Undo",
                onClick = { viewModel.undo() }
            )
            SolverActionButton(
                icon = Icons.Filled.Close,
                label = "Clear",
                isActive = state.isClearMode,
                onClick = { viewModel.toggleClearMode() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Number row
        NumberRow(
            board = state.board,
            selectedDigit = state.selectedDigit,
            onDigitSelected = { digit -> viewModel.selectDigit(digit) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Solve button — full width, orange accent
        Button(
            onClick = { viewModel.solve() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange
            )
        ) {
            Text(
                text = "Solve",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (state.noSolution) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No solution found",
                fontSize = 15.sp,
                color = if (isDark) ConflictColorDark else ConflictColorLight,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SolverActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    val tint = if (isActive) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onBackground

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = tint
        )
    }
}
