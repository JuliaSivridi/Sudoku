package com.example.sudoku.data

import android.content.Context
import com.example.sudoku.model.Cell
import com.example.sudoku.model.Difficulty
import com.example.sudoku.model.GameState
import org.json.JSONArray
import org.json.JSONObject

class GameSaveManager(context: Context) {

    private val prefs = context.getSharedPreferences("sudoku_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_GAME = "saved_game"
    }

    fun saveGame(state: GameState) {
        try {
            val root = JSONObject()

            root.put("difficulty", state.difficulty.name)

            // Board: 9x9 array of cell objects
            val boardJson = JSONArray()
            for (row in state.board) {
                val rowJson = JSONArray()
                for (cell in row) {
                    val cellJson = JSONObject()
                    cellJson.put("v", cell.value)
                    cellJson.put("g", cell.isGiven)
                    val notesJson = JSONArray()
                    for (note in cell.notes) {
                        notesJson.put(note)
                    }
                    cellJson.put("n", notesJson)
                    rowJson.put(cellJson)
                }
                boardJson.put(rowJson)
            }
            root.put("board", boardJson)

            // Solution: 9x9 array of ints
            val solutionJson = JSONArray()
            for (row in state.solution) {
                val rowJson = JSONArray()
                for (value in row) {
                    rowJson.put(value)
                }
                solutionJson.put(rowJson)
            }
            root.put("solution", solutionJson)

            // Таймер, ошибки, подсказки
            root.put("timer_enabled", state.timerEnabled)
            root.put("elapsed", state.elapsedSeconds)
            root.put("error_limit", state.errorLimit)
            root.put("errors", state.errorCount)
            root.put("hints_remaining", state.hintsRemaining)

            prefs.edit().putString(KEY_SAVED_GAME, root.toString()).apply()
        } catch (e: Exception) {
            // Если сериализация не удалась — оставляем предыдущее сохранение
        }
    }

    fun loadGame(): GameState? {
        val json = prefs.getString(KEY_SAVED_GAME, null) ?: return null
        return try {
            val root = JSONObject(json)

            val difficulty = Difficulty.valueOf(root.getString("difficulty"))

            val boardJson = root.getJSONArray("board")
            val board = List(9) { r ->
                val rowJson = boardJson.getJSONArray(r)
                List(9) { c ->
                    val cellJson = rowJson.getJSONObject(c)
                    val notesJson = cellJson.getJSONArray("n")
                    val notes = mutableSetOf<Int>()
                    for (i in 0 until notesJson.length()) {
                        notes.add(notesJson.getInt(i))
                    }
                    Cell(
                        value = cellJson.getInt("v"),
                        isGiven = cellJson.getBoolean("g"),
                        notes = notes
                    )
                }
            }

            val solutionJson = root.getJSONArray("solution")
            val solution = List(9) { r ->
                val rowJson = solutionJson.getJSONArray(r)
                List(9) { c -> rowJson.getInt(c) }
            }

            // Обратная совместимость: поля отсутствуют в старых сохранениях
            val timerEnabled = if (root.has("timer_enabled")) root.getBoolean("timer_enabled") else false
            val elapsedSeconds = if (root.has("elapsed")) root.getInt("elapsed") else 0
            val errorLimit = if (root.has("error_limit")) root.getInt("error_limit") else 0
            val errorCount = if (root.has("errors")) root.getInt("errors") else 0
            val hintsRemaining = if (root.has("hints_remaining")) root.getInt("hints_remaining") else -1

            GameState(
                board = board,
                solution = solution,
                difficulty = difficulty,
                selectedCell = null,
                selectedDigit = null,
                inputMode = com.example.sudoku.model.InputMode.NORMAL,
                isComplete = false,
                undoStack = emptyList(),
                timerEnabled = timerEnabled,
                elapsedSeconds = elapsedSeconds,
                errorLimit = errorLimit,
                errorCount = errorCount,
                hintsRemaining = hintsRemaining,
            )
        } catch (e: Exception) {
            null
        }
    }

    fun hasSavedGame(): Boolean {
        return prefs.contains(KEY_SAVED_GAME)
    }

    fun clearSavedGame() {
        prefs.edit().remove(KEY_SAVED_GAME).apply()
    }
}
