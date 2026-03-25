package com.example.sudoku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sudoku.data.ThemeManager
import com.example.sudoku.ui.theme.AppColorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val themeManager = ThemeManager(application)
    private val _currentTheme = MutableStateFlow(themeManager.getTheme())
    val currentTheme: StateFlow<AppColorTheme> = _currentTheme.asStateFlow()

    fun setTheme(theme: AppColorTheme) {
        themeManager.saveTheme(theme)
        _currentTheme.value = theme
    }
}
