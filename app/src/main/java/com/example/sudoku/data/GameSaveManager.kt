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

            prefs.edit().putString(KEY_SAVED_GAME, root.toString()).apply()
        } catch (e: Exception) {
            // If serialization fails, leave any existing save intact
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

            GameState(
                board = board,
                solution = solution,
                difficulty = difficulty,
                selectedCell = null,
                selectedDigit = null,
                inputMode = com.example.sudoku.model.InputMode.NORMAL,
                isComplete = false,
                undoStack = emptyList()
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
