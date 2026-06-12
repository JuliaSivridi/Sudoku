package com.example.sudoku.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
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
    timerEnabled: Boolean,
    digitCountEnabled: Boolean,
    onGameComplete: () -> Unit,
    onGameLost: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) onGameComplete()
    }
    LaunchedEffect(state.isLost) {
        if (state.isLost) onGameLost()
    }

    // Свернули приложение — стоп таймера + сохранение; вернулись — перезапуск
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.onAppBackground()
                Lifecycle.Event.ON_START -> viewModel.onAppForeground()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Поле генерируется в фоне — показываем спиннер вместо пустой доски
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isCellFirst = inputPreference == InputPreference.CELL_FIRST
    val isPaused = state.isTimerPaused

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Единая строка заголовка: [ошибки] | [уровень] | [таймер + пауза] ──────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая зона: счётчик ошибок, выровнен влево
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (state.errorLimit > 0) {
                    Icon(
                        imageVector = Icons.Outlined.Error,
                        contentDescription = "Errors",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.errorCount}/${state.errorLimit}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                    )
                }
            }

            // Центр: название уровня сложности
            Text(
                text = state.difficulty.label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Правая зона: таймер + кнопка паузы, выровнены вправо
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (timerEnabled) {
                    Text(
                        text = formatTime(state.elapsedSeconds),
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Кнопка паузы — визуально как кнопки управления (иконка 28dp)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                if (isPaused) viewModel.resumeTimer()
                                else viewModel.pauseTimer()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Сетка судоку
        SudokuGrid(
            state = state,
            onCellTap = { row, col ->
                if (!isPaused) {
                    if (isCellFirst) viewModel.selectCell(row, col)
                    else viewModel.onCellTap(row, col)
                }
            },
            isPaused = isPaused,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки управления
        ControlButtons(
            inputMode = state.inputMode,
            isCellFirst = isCellFirst,
            isAutoNotesActive = state.autoNotesActive,
            isEnabled = !isPaused,
            hintsRemaining = state.hintsRemaining,
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
            showDigitCount = digitCountEnabled,
            isEnabled = !isPaused,
            onDigitSelected = { digit ->
                if (isCellFirst) viewModel.placeDigit(digit)
                else viewModel.selectDigit(digit)
            }
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}
