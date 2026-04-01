package com.example.sudoku.data

import android.content.Context
import com.example.sudoku.model.InputPreference

class InputPreferenceManager(context: Context) {
    private val prefs = context.getSharedPreferences("sudoku_prefs", Context.MODE_PRIVATE)

    fun getPreference(): InputPreference {
        val name = prefs.getString("input_preference", InputPreference.NUMBER_FIRST.name)
            ?: InputPreference.NUMBER_FIRST.name
        return try { InputPreference.valueOf(name) } catch (e: Exception) { InputPreference.NUMBER_FIRST }
    }

    fun savePreference(preference: InputPreference) {
        prefs.edit().putString("input_preference", preference.name).apply()
    }
}
