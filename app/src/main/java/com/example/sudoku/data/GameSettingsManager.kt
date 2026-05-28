package com.example.sudoku.data

import android.content.Context

class GameSettingsManager(context: Context) {

    private val prefs = context.getSharedPreferences("sudoku_settings", Context.MODE_PRIVATE)

    fun isTimerEnabled(): Boolean = prefs.getBoolean("timer_enabled", false)
    fun setTimerEnabled(v: Boolean) = prefs.edit().putBoolean("timer_enabled", v).apply()

    fun isErrorLimitEnabled(): Boolean = prefs.getBoolean("error_limit_enabled", false)
    fun setErrorLimitEnabled(v: Boolean) = prefs.edit().putBoolean("error_limit_enabled", v).apply()

    fun getErrorLimit(): Int = prefs.getInt("error_limit", 3)
    fun setErrorLimit(v: Int) = prefs.edit().putInt("error_limit", v.coerceAtLeast(1)).apply()

    fun isHintLimitEnabled(): Boolean = prefs.getBoolean("hint_limit_enabled", false)
    fun setHintLimitEnabled(v: Boolean) = prefs.edit().putBoolean("hint_limit_enabled", v).apply()

    fun getHintLimit(): Int = prefs.getInt("hint_limit", 5)
    fun setHintLimit(v: Int) = prefs.edit().putInt("hint_limit", v.coerceAtLeast(1)).apply()

    fun isDigitCountEnabled(): Boolean = prefs.getBoolean("digit_count_enabled", false)
    fun setDigitCountEnabled(v: Boolean) = prefs.edit().putBoolean("digit_count_enabled", v).apply()
}
