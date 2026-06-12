package com.example.sudoku.model

enum class InputMode { NORMAL, ERASE, NOTES }

// Снимок для отмены: доска + состояние кнопки Clues на момент хода
data class UndoEntry(
    val board: List<List<Cell>>,
    val autoNotesActive: Boolean,
)

data class GameState(
    val board: List<List<Cell>> = List(9) { List(9) { Cell() } },
    val solution: List<List<Int>> = List(9) { List(9) { 0 } },
    val difficulty: Difficulty = Difficulty.EASY,
    val selectedCell: Pair<Int, Int>? = null,
    val selectedDigit: Int? = null,
    val inputMode: InputMode = InputMode.NORMAL,
    val isComplete: Boolean = false,
    val isLoading: Boolean = false,
    val autoNotesActive: Boolean = false,
    val undoStack: List<UndoEntry> = emptyList(),
    // Таймер
    val timerEnabled: Boolean = false,
    val elapsedSeconds: Int = 0,
    val isTimerPaused: Boolean = false,
    // Лимит ошибок
    val errorLimit: Int = 0,   // 0 = без лимита
    val errorCount: Int = 0,
    val isLost: Boolean = false,
    // Лимит подсказок: -1 = безлимитно, 0 = исчерпаны, >0 = осталось N
    val hintsRemaining: Int = -1,
)
