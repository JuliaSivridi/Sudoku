package com.example.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudoku.game.SudokuGenerator
import com.example.sudoku.model.Cell
import com.example.sudoku.model.Difficulty
import com.example.sudoku.model.GameState
import com.example.sudoku.model.InputMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    // Начать новую игру
    fun startGame(difficulty: Difficulty) {
        val (board, solution) = SudokuGenerator.generate(difficulty)
        _state.value = GameState(
            board = board,
            solution = solution,
            difficulty = difficulty
        )
    }

    // Выбрать цифру из нижнего ряда — снимает выделение ячейки
    fun selectDigit(digit: Int) {
        val current = _state.value
        val newDigit = if (current.selectedDigit == digit) null else digit
        _state.value = current.copy(
            selectedDigit = newDigit,
            selectedCell = null,
            inputMode = if (current.inputMode == InputMode.ERASE) InputMode.NORMAL else current.inputMode
        )
    }

    // Выбрать/нажать ячейку
    fun onCellTap(row: Int, col: Int) {
        val current = _state.value
        val cell = current.board[row][col]

        when (current.inputMode) {
            InputMode.ERASE -> {
                if (!cell.isGiven) {
                    val undoStack = buildUndoStack(current)
                    val newBoard = current.board.updateCell(row, col) { Cell() }
                    _state.value = current.copy(
                        board = newBoard,
                        selectedCell = Pair(row, col),
                        undoStack = undoStack
                    )
                }
            }
            InputMode.NOTES -> {
                if (!cell.isGiven && cell.value == 0 && current.selectedDigit != null) {
                    val digit = current.selectedDigit
                    val undoStack = buildUndoStack(current)
                    val newBoard = current.board.updateCell(row, col) {
                        val newNotes = if (digit in it.notes) it.notes - digit else it.notes + digit
                        it.copy(value = 0, notes = newNotes)
                    }
                    _state.value = current.copy(
                        board = newBoard,
                        selectedCell = Pair(row, col),
                        undoStack = undoStack
                    )
                } else {
                    _state.value = current.copy(selectedCell = Pair(row, col))
                }
            }
            InputMode.NORMAL -> {
                if (!cell.isGiven && cell.value == 0 && current.selectedDigit != null) {
                    val undoStack = buildUndoStack(current)
                    var newBoard = current.board.updateCell(row, col) {
                        Cell(value = current.selectedDigit, isGiven = false)
                    }
                    newBoard = cleanNotesAfterPlacement(newBoard, row, col, current.selectedDigit)
                    val newState = current.copy(
                        board = newBoard,
                        selectedCell = Pair(row, col),
                        selectedDigit = deselectIfFull(current.selectedDigit, newBoard),
                        undoStack = undoStack
                    )
                    _state.value = newState.copy(isComplete = checkComplete(newState))
                } else {
                    _state.value = current.copy(selectedCell = Pair(row, col))
                }
            }
        }
    }

    // Переключить режим стирания
    fun toggleErase() {
        val current = _state.value
        val isErase = current.inputMode == InputMode.ERASE
        _state.value = current.copy(
            inputMode = if (isErase) InputMode.NORMAL else InputMode.ERASE,
            selectedDigit = null
        )
    }

    // Переключить режим заметок
    fun toggleNotes() {
        val current = _state.value
        val isNotes = current.inputMode == InputMode.NOTES
        _state.value = current.copy(
            inputMode = if (isNotes) InputMode.NORMAL else InputMode.NOTES
        )
    }

    // Отмена последнего действия
    fun undo() {
        val current = _state.value
        if (current.undoStack.isEmpty()) return
        val previousBoard = current.undoStack.last()
        _state.value = current.copy(
            board = previousBoard,
            undoStack = current.undoStack.dropLast(1),
            isComplete = false
        )
    }

    // Подсказка: заполнить одну пустую ячейку
    fun hint() {
        val current = _state.value
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0..8) {
            for (c in 0..8) {
                if (current.board[r][c].value == 0) emptyCells.add(Pair(r, c))
            }
        }
        if (emptyCells.isEmpty()) return

        val (row, col) = emptyCells.random()
        val correctValue = current.solution[row][col]
        val undoStack = buildUndoStack(current)
        var newBoard = current.board.updateCell(row, col) {
            Cell(value = correctValue, isGiven = false, notes = emptySet())
        }
        newBoard = cleanNotesAfterPlacement(newBoard, row, col, correctValue)
        val newState = current.copy(
            board = newBoard,
            selectedCell = Pair(row, col),
            selectedDigit = current.selectedDigit?.let { deselectIfFull(it, newBoard) },
            undoStack = undoStack
        )
        _state.value = newState.copy(isComplete = checkComplete(newState))
    }

    // Убрать цифру digit из заметок в той же строке и столбце.
    // Если цифра расставлена 9 раз — убрать из всех заметок на доске.
    private fun cleanNotesAfterPlacement(
        board: List<List<Cell>>,
        row: Int,
        col: Int,
        digit: Int
    ): List<List<Cell>> {
        val totalCount = board.flatten().count { it.value == digit }
        val removeEverywhere = totalCount >= 9
        val boxRowStart = (row / 3) * 3
        val boxColStart = (col / 3) * 3
        return board.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                if (digit in cell.notes) {
                    val inSameBox = r in boxRowStart until boxRowStart + 3 &&
                            c in boxColStart until boxColStart + 3
                    val shouldRemove = removeEverywhere || r == row || c == col || inSameBox
                    if (shouldRemove) cell.copy(notes = cell.notes - digit) else cell
                } else cell
            }
        }
    }

    // Снять выбор цифры, если она заполнена 9 раз на доске
    private fun deselectIfFull(digit: Int, board: List<List<Cell>>): Int? {
        val count = board.flatten().count { it.value == digit }
        return if (count >= 9) null else digit
    }

    // Построить стек отмены: добавить текущую доску, ограничить глубину 50
    private fun buildUndoStack(state: GameState): List<List<List<Cell>>> =
        (state.undoStack + listOf(state.board)).takeLast(50)

    // Проверить завершение игры
    private fun checkComplete(state: GameState): Boolean {
        for (r in 0..8) {
            for (c in 0..8) {
                val cell = state.board[r][c]
                if (cell.value == 0 || cell.value != state.solution[r][c]) return false
            }
        }
        return true
    }
}

// Вспомогательное расширение для иммутабельного обновления ячейки
private fun List<List<Cell>>.updateCell(
    row: Int, col: Int,
    update: (Cell) -> Cell
): List<List<Cell>> {
    return mapIndexed { r, rowList ->
        if (r == row) rowList.mapIndexed { c, cell ->
            if (c == col) update(cell) else cell
        } else rowList
    }
}
