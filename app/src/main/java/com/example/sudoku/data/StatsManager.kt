package com.example.sudoku.data

import android.content.Context
import com.example.sudoku.model.Difficulty

class StatsManager(context: Context) {

    private val prefs = context.getSharedPreferences("sudoku_stats", Context.MODE_PRIVATE)

    fun increment(difficulty: Difficulty) {
        val current = prefs.getInt(difficulty.name, 0)
        prefs.edit().putInt(difficulty.name, current + 1).apply()
    }

    fun getCount(difficulty: Difficulty): Int = prefs.getInt(difficulty.name, 0)

    /** Лучшее время в секундах, null если ещё не зафиксировано. */
    fun getBestTime(difficulty: Difficulty): Long? {
        val v = prefs.getLong("best_${difficulty.name}", -1L)
        return if (v < 0L) null else v
    }

    /** Обновляет лучшее время, если переданное значение лучше текущего. */
    fun updateBestTime(difficulty: Difficulty, seconds: Long) {
        val current = getBestTime(difficulty)
        if (current == null || seconds < current) {
            prefs.edit().putLong("best_${difficulty.name}", seconds).apply()
        }
    }
}
