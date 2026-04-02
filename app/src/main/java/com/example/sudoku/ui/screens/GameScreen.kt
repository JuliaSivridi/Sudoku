package com.example.sudoku.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.InputPreference
import com.example.sudoku.ui.components.ControlButtons
import com.example.sudoku.ui.components.NumberRow
import com.example.sudoku.ui.components.SudokuGrid
import com.example.sudoku.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    inputPreference: InputPreference,
    onGameComplete: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onGameComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Верхняя панель: кнопка назад + название уровня
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
                text = state.difficulty.label,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val isCellFirst = inputPreference == InputPreference.CELL_FIRST

        // Сетка судоку
        SudokuGrid(
            state = state,
            onCellTap = { row, col ->
                if (isCellFirst) viewModel.selectCell(row, col)
                else viewModel.onCellTap(row, col)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки управления
        ControlButtons(
            inputMode = state.inputMode,
            isCellFirst = isCellFirst,
            isAutoNotesActive = state.autoNotesActive,
            onUndo = { viewModel.undo() },
            onToggleErase = {
                if (isCellFirst) viewModel.eraseSelected()
                else viewModel.toggleErase()
            },
            onToggleNotes = { viewModel.toggleNotes() },
            onToggleAutoNotes = { viewModel.toggleAutoNotes() },
            onHint = { viewModel.hint() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ряд цифр
        NumberRow(
            board = state.board,
            selectedDigit = state.selectedDigit,
            showSelection = !isCellFirst,
            onDigitSelected = { digit ->
                if (isCellFirst) viewModel.placeDigit(digit)
                else viewModel.selectDigit(digit)
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
