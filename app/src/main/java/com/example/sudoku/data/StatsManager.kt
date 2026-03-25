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
}
