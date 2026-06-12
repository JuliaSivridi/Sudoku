package com.example.sudoku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.data.GameSaveManager
import com.example.sudoku.data.StatsManager
import com.example.sudoku.game.SudokuGenerator
import com.example.sudoku.model.Cell
import com.example.sudoku.model.Difficulty
import com.example.sudoku.model.GameState
import com.example.sudoku.model.InputMode
import com.example.sudoku.model.UndoEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val saveManager = GameSaveManager(application)
    private val statsManager = StatsManager(application)
    private var statsRecorded = false
    private var timerJob: Job? = null
    // Снапшот последней сохранённой доски — сохраняем только при реальных изменениях
    private var lastSavedBoardSnapshot: List<List<Cell>>? = null

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            state.collect { gameState ->
                when {
                    gameState.isComplete -> {
                        saveManager.clearSavedGame()
                        if (!statsRecorded) {
                            statsRecorded = true
                            statsManager.increment(gameState.difficulty)
                            if (gameState.timerEnabled) {
                                statsManager.updateBestTime(
                                    gameState.difficulty,
                                    gameState.elapsedSeconds.toLong()
                                )
                            }
                        }
                    }
                    gameState.isLost -> saveManager.clearSavedGame()
                    gameState.board != lastSavedBoardSnapshot &&
                        gameState.board.any { row -> row.any { !it.isGiven && it.value != 0 } } -> {
                        lastSavedBoardSnapshot = gameState.board
                        saveManager.saveGame(gameState)
                    }
                }
            }
        }
    }

    // Начать новую игру
    fun startGame(
        difficulty: Difficulty,
        timerEnabled: Boolean = false,
        errorLimit: Int = 0,
        hintLimit: Int = 0,
    ) {
        statsRecorded = false
        lastSavedBoardSnapshot = null
        timerJob?.cancel()
        // Показываем спиннер сразу, генерируем в фоне: backtracking + проверка
        // уникальности на каждое удаление могут подвесить UI-поток
        _state.value = GameState(difficulty = difficulty, isLoading = true)
        viewModelScope.launch(Dispatchers.Default) {
            val (board, solution) = SudokuGenerator.generate(difficulty)
            _state.value = GameState(
                board = board,
                solution = solution,
                difficulty = difficulty,
                timerEnabled = timerEnabled,
                errorLimit = errorLimit,
                hintsRemaining = if (hintLimit > 0) hintLimit else -1,
            )
            if (timerEnabled) startTimerCoroutine()
        }
    }

    // Загрузить сохранённую игру. false = сохранение отсутствует или битое.
    fun loadSavedGame(): Boolean {
        statsRecorded = false
        lastSavedBoardSnapshot = null
        timerJob?.cancel()
        val loaded = saveManager.loadGame()
        if (loaded == null) {
            // Битый JSON: убираем сохранение, чтобы кнопка Continue не вела в пустую игру
            saveManager.clearSavedGame()
            return false
        }
        _state.value = loaded.copy(
            selectedCell = null,
            selectedDigit = null,
            inputMode = InputMode.NORMAL,
            isComplete = false,
            isTimerPaused = false,
            undoStack = emptyList()
        )
        if (loaded.timerEnabled) startTimerCoroutine()
        return true
    }

    // Приложение свёрнуто: останавливаем таймер (фоновое время не считается)
    // и сохраняем игру, чтобы не потерять секунды после последнего хода
    fun onAppBackground() {
        timerJob?.cancel()
        val s = _state.value
        val inProgress = !s.isComplete && !s.isLost && !s.isLoading &&
            s.solution.any { row -> row.any { it != 0 } }
        if (inProgress) saveManager.saveGame(s)
    }

    // Приложение снова на экране: перезапускаем таймер, если игра активна
    fun onAppForeground() {
        val s = _state.value
        if (s.timerEnabled && !s.isComplete && !s.isLost && timerJob?.isActive != true) {
            startTimerCoroutine()
        }
    }

    // Таймер: тикает каждую секунду пока игра активна и не на паузе
    private fun startTimerCoroutine() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val s = _state.value
                when {
                    s.isComplete || s.isLost -> break
                    !s.isTimerPaused -> _state.value = s.copy(elapsedSeconds = s.elapsedSeconds + 1)
                }
            }
        }
    }

    fun pauseTimer() {
        _state.value = _state.value.copy(isTimerPaused = true)
    }

    fun resumeTimer() {
        _state.value = _state.value.copy(isTimerPaused = false)
    }

    // Выбрать цифру из нижнего ряда — снимает выделение ячейки
    fun selectDigit(digit: Int) {
        val current = _state.value
        if (current.isTimerPaused) return
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
        if (current.isTimerPaused) return
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
                    val newDigit = if (cell.value != 0) cell.value else current.selectedDigit
                    _state.value = current.copy(selectedCell = Pair(row, col), selectedDigit = newDigit)
                }
            }
            InputMode.NORMAL -> {
                if (!cell.isGiven && cell.value == 0 && current.selectedDigit != null) {
                    val placedDigit = current.selectedDigit
                    val isWrong = placedDigit != current.solution[row][col]
                    val newErrorCount = if (isWrong) current.errorCount + 1 else current.errorCount
                    val newIsLost = current.errorLimit > 0 && newErrorCount >= current.errorLimit

                    val undoStack = buildUndoStack(current)
                    var newBoard = current.board.updateCell(row, col) {
                        Cell(value = placedDigit, isGiven = false)
                    }
                    newBoard = cleanNotesAfterPlacement(newBoard, row, col, placedDigit)
                    val newState = current.copy(
                        board = newBoard,
                        selectedCell = Pair(row, col),
                        selectedDigit = deselectIfFull(placedDigit, newBoard),
                        undoStack = undoStack,
                        errorCount = newErrorCount,
                        isLost = newIsLost,
                    )
                    _state.value = if (newIsLost) newState
                                   else newState.copy(isComplete = checkComplete(newState))
                } else {
                    val newDigit = if (cell.value != 0) cell.value else current.selectedDigit
                    _state.value = current.copy(selectedCell = Pair(row, col), selectedDigit = newDigit)
                }
            }
        }
    }

    // ── Cell First: выбрать ячейку (без размещения цифры) ──────────────────────
    fun selectCell(row: Int, col: Int) {
        val current = _state.value
        if (current.isTimerPaused) return
        val cell = current.board[row][col]
        val newDigit = if (cell.value != 0) cell.value else null
        _state.value = current.copy(
            selectedCell = Pair(row, col),
            selectedDigit = newDigit
        )
    }

    // Cell First: поставить цифру или переключить заметку в выбранной ячейке
    fun placeDigit(digit: Int) {
        val current = _state.value
        if (current.isTimerPaused) return
        val selCell = current.selectedCell ?: return
        val (row, col) = selCell
        val cell = current.board[row][col]
        if (cell.isGiven) return

        when (current.inputMode) {
            InputMode.NOTES -> {
                if (cell.value == 0) {
                    val undoStack = buildUndoStack(current)
                    val newBoard = current.board.updateCell(row, col) {
                        val newNotes = if (digit in it.notes) it.notes - digit else it.notes + digit
                        it.copy(notes = newNotes)
                    }
                    _state.value = current.copy(board = newBoard, selectedCell = selCell, undoStack = undoStack)
                }
            }
            InputMode.NORMAL -> {
                if (cell.value == 0) {
                    val isWrong = digit != current.solution[row][col]
                    val newErrorCount = if (isWrong) current.errorCount + 1 else current.errorCount
                    val newIsLost = current.errorLimit > 0 && newErrorCount >= current.errorLimit

                    val undoStack = buildUndoStack(current)
                    var newBoard = current.board.updateCell(row, col) {
                        Cell(value = digit, isGiven = false)
                    }
                    newBoard = cleanNotesAfterPlacement(newBoard, row, col, digit)
                    val newState = current.copy(
                        board = newBoard,
                        selectedCell = selCell,
                        selectedDigit = deselectIfFull(digit, newBoard),
                        undoStack = undoStack,
                        errorCount = newErrorCount,
                        isLost = newIsLost,
                    )
                    _state.value = if (newIsLost) newState
                                   else newState.copy(isComplete = checkComplete(newState))
                }
            }
            InputMode.ERASE -> { /* не используется в Cell First */ }
        }
    }

    // Cell First: стереть выбранную ячейку напрямую (без переключения режима)
    fun eraseSelected() {
        val current = _state.value
        if (current.isTimerPaused) return
        val selCell = current.selectedCell ?: return
        val (row, col) = selCell
        val cell = current.board[row][col]
        if (cell.isGiven) return
        if (cell.value == 0 && cell.notes.isEmpty()) return
        val undoStack = buildUndoStack(current)
        val newBoard = current.board.updateCell(row, col) { Cell() }
        _state.value = current.copy(
            board = newBoard,
            selectedCell = selCell,
            selectedDigit = null,
            undoStack = undoStack
        )
    }

    // Переключить режим стирания
    fun toggleErase() {
        val current = _state.value
        if (current.isTimerPaused) return
        val isErase = current.inputMode == InputMode.ERASE
        _state.value = current.copy(
            inputMode = if (isErase) InputMode.NORMAL else InputMode.ERASE,
            selectedDigit = null
        )
    }

    // Переключить режим заметок
    fun toggleNotes() {
        val current = _state.value
        if (current.isTimerPaused) return
        val isNotes = current.inputMode == InputMode.NOTES
        _state.value = current.copy(
            inputMode = if (isNotes) InputMode.NORMAL else InputMode.NOTES
        )
    }

    // Отмена последнего действия
    fun undo() {
        val current = _state.value
        if (current.isTimerPaused) return
        if (current.undoStack.isEmpty()) return
        val previous = current.undoStack.last()
        _state.value = current.copy(
            board = previous.board,
            autoNotesActive = previous.autoNotesActive,
            undoStack = current.undoStack.dropLast(1),
            isComplete = false
        )
    }

    // Clues: тоггл — включить (заполнить все возможные заметки) / выключить (очистить все заметки)
    fun toggleAutoNotes() {
        val current = _state.value
        if (current.isTimerPaused) return
        val undoStack = buildUndoStack(current)
        if (!current.autoNotesActive) {
            val newBoard = current.board.mapIndexed { r, row ->
                row.mapIndexed { c, cell ->
                    if (cell.value == 0) {
                        val possible = (1..9).filter { digit ->
                            canPlace(current.board, r, c, digit)
                        }.toSet()
                        cell.copy(notes = possible)
                    } else cell
                }
            }
            _state.value = current.copy(board = newBoard, autoNotesActive = true, undoStack = undoStack)
        } else {
            val newBoard = current.board.map { row ->
                row.map { cell -> if (cell.notes.isNotEmpty()) cell.copy(notes = emptySet()) else cell }
            }
            _state.value = current.copy(board = newBoard, autoNotesActive = false, undoStack = undoStack)
        }
    }

    // Подсказка: если выбрана пустая ячейка — ставим туда; иначе — в случайную пустую
    fun hint() {
        val current = _state.value
        if (current.isTimerPaused) return
        if (current.hintsRemaining == 0) return  // подсказки исчерпаны

        val selCell = current.selectedCell
        val target: Pair<Int, Int>
        if (selCell != null && current.board[selCell.first][selCell.second].value == 0) {
            target = selCell
        } else {
            val emptyCells = mutableListOf<Pair<Int, Int>>()
            for (r in 0..8) {
                for (c in 0..8) {
                    if (current.board[r][c].value == 0) emptyCells.add(Pair(r, c))
                }
            }
            if (emptyCells.isEmpty()) return
            target = emptyCells.random()
        }

        val (row, col) = target
        val correctValue = current.solution[row][col]
        val undoStack = buildUndoStack(current)
        var newBoard = current.board.updateCell(row, col) {
            Cell(value = correctValue, isGiven = false, notes = emptySet())
        }
        newBoard = cleanNotesAfterPlacement(newBoard, row, col, correctValue)
        val newHintsRemaining = if (current.hintsRemaining > 0) current.hintsRemaining - 1 else -1
        val newState = current.copy(
            board = newBoard,
            selectedCell = Pair(row, col),
            selectedDigit = current.selectedDigit?.let { deselectIfFull(it, newBoard) },
            undoStack = undoStack,
            hintsRemaining = newHintsRemaining,
        )
        _state.value = newState.copy(isComplete = checkComplete(newState))
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    // Убрать цифру digit из заметок в той же строке, столбце и квадрате.
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

    // Проверить, можно ли поставить digit в ячейку (row, col)
    private fun canPlace(board: List<List<Cell>>, row: Int, col: Int, digit: Int): Boolean {
        if (board[row].any { it.value == digit }) return false
        if (board.any { it[col].value == digit }) return false
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (board[r][c].value == digit) return false
            }
        }
        return true
    }

    // Построить стек отмены: добавить текущий снимок, ограничить глубину 50
    private fun buildUndoStack(state: GameState): List<UndoEntry> =
        (state.undoStack + UndoEntry(state.board, state.autoNotesActive)).takeLast(50)

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
