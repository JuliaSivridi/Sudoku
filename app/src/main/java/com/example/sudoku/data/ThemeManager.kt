package com.example.sudoku.data

import android.content.Context
import com.example.sudoku.ui.theme.AppColorTheme

class ThemeManager(context: Context) {
    private val prefs = context.getSharedPreferences("sudoku_prefs", Context.MODE_PRIVATE)

    fun getTheme(): AppColorTheme {
        val name = prefs.getString("color_theme", AppColorTheme.ORANGE.name)
            ?: AppColorTheme.ORANGE.name
        return try { AppColorTheme.valueOf(name) } catch (e: Exception) { AppColorTheme.ORANGE }
    }

    fun saveTheme(theme: AppColorTheme) {
        prefs.edit().putString("color_theme", theme.name).apply()
    }
}
