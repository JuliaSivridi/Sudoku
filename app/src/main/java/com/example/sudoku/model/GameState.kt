package com.example.sudoku.model

enum class InputMode { NORMAL, ERASE, NOTES }

data class GameState(
    val board: List<List<Cell>> = List(9) { List(9) { Cell() } },
    val solution: List<List<Int>> = List(9) { List(9) { 0 } },
    val difficulty: Difficulty = Difficulty.EASY,
    val selectedCell: Pair<Int, Int>? = null,
    val selectedDigit: Int? = null,
    val inputMode: InputMode = InputMode.NORMAL,
    val isComplete: Boolean = false,
    val autoNotesActive: Boolean = false,
    val undoStack: List<List<List<Cell>>> = emptyList()
)
