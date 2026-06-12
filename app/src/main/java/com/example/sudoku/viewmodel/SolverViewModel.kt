package com.example.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.game.SudokuSolver
import com.example.sudoku.model.Cell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SolverState(
    val board: List<List<Cell>> = List(9) { List(9) { Cell() } },
    val selectedDigit: Int? = null,
    val isClearMode: Boolean = false,
    val isSolved: Boolean = false,
    val noSolution: Boolean = false,
    val undoStack: List<List<List<Cell>>> = emptyList()
)

class SolverViewModel : ViewModel() {

    private val _state = MutableStateFlow(SolverState())
    val state: StateFlow<SolverState> = _state.asStateFlow()

    fun selectDigit(digit: Int) {
        val current = _state.value
        val newDigit = if (current.selectedDigit == digit) null else digit
        _state.value = current.copy(
            selectedDigit = newDigit,
            isClearMode = false
        )
    }

    fun toggleClearMode() {
        val current = _state.value
        _state.value = current.copy(
            isClearMode = !current.isClearMode,
            selectedDigit = null
        )
    }

    fun onCellTap(row: Int, col: Int) {
        val current = _state.value
        val cell = current.board[row][col]

        when {
            current.isClearMode -> {
                val undoStack = buildUndoStack(current)
                val newBoard = current.board.solverUpdateCell(row, col) { Cell() }
                _state.value = current.copy(
                    board = newBoard,
                    undoStack = undoStack,
                    isSolved = false,
                    noSolution = false
                )
            }
            current.selectedDigit != null && cell.value == 0 -> {
                val undoStack = buildUndoStack(current)
                val newBoard = current.board.solverUpdateCell(row, col) {
                    Cell(value = current.selectedDigit, isGiven = false)
                }
                _state.value = current.copy(
                    board = newBoard,
                    undoStack = undoStack,
                    isSolved = false,
                    noSolution = false
                )
            }
            else -> {
                // do nothing — no cell selection concept in solver
            }
        }
    }

    fun clearAll() {
        val current = _state.value
        val undoStack = buildUndoStack(current)
        _state.value = current.copy(
            board = List(9) { List(9) { Cell() } },
            undoStack = undoStack,
            isSolved = false,
            noSolution = false
        )
    }

    fun undo() {
        val current = _state.value
        if (current.undoStack.isEmpty()) return
        val previousBoard = current.undoStack.last()
        _state.value = current.copy(
            board = previousBoard,
            undoStack = current.undoStack.dropLast(1),
            isSolved = false,
            noSolution = false
        )
    }

    fun solve() {
        val current = _state.value
        // Convert board to Array<IntArray> for solver
        val grid = Array(9) { r -> IntArray(9) { c -> current.board[r][c].value } }

        // Введённые цифры конфликтуют между собой? Солвер заполняет только пустые
        // клетки и сам этого не заметит — «решение» нарушало бы правила судоку.
        // Конфликтующие клетки уже подсвечены красным на поле — просто не решаем.
        if (hasConflicts(grid)) return

        // В фоне: на «вредных» расстановках backtracking может занять секунды
        viewModelScope.launch(Dispatchers.Default) {
            val solvable = SudokuSolver.solve(grid)
            if (!solvable) {
                _state.value = current.copy(noSolution = true, isSolved = false)
                return@launch
            }
            val undoStack = buildUndoStack(current)
            val newBoard = List(9) { r ->
                List(9) { c ->
                    val originalCell = current.board[r][c]
                    if (originalCell.value != 0) {
                        originalCell
                    } else {
                        Cell(value = grid[r][c], isGiven = false)
                    }
                }
            }
            _state.value = current.copy(
                board = newBoard,
                undoStack = undoStack,
                isSolved = true,
                noSolution = false
            )
        }
    }

    // true, если какая-то введённая цифра повторяется в своей строке/столбце/квадрате
    private fun hasConflicts(grid: Array<IntArray>): Boolean {
        for (r in 0..8) {
            for (c in 0..8) {
                val v = grid[r][c]
                if (v != 0) {
                    grid[r][c] = 0
                    val ok = SudokuSolver.isValid(grid, r, c, v)
                    grid[r][c] = v
                    if (!ok) return true
                }
            }
        }
        return false
    }

    private fun buildUndoStack(state: SolverState): List<List<List<Cell>>> =
        (state.undoStack + listOf(state.board)).takeLast(50)
}

private fun List<List<Cell>>.solverUpdateCell(
    row: Int, col: Int,
    update: (Cell) -> Cell
): List<List<Cell>> {
    return mapIndexed { r, rowList ->
        if (r == row) rowList.mapIndexed { c, cell ->
            if (c == col) update(cell) else cell
        } else rowList
    }
}
