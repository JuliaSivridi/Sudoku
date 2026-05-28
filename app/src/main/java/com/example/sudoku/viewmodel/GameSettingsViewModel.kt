package com.example.sudoku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sudoku.data.GameSettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameSettings(
    val timerEnabled: Boolean = false,
    val errorLimitEnabled: Boolean = false,
    val errorLimit: Int = 3,
    val hintLimitEnabled: Boolean = false,
    val hintLimit: Int = 5,
    val digitCountEnabled: Boolean = false,
)

class GameSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val manager = GameSettingsManager(application)

    private val _settings = MutableStateFlow(
        GameSettings(
            timerEnabled = manager.isTimerEnabled(),
            errorLimitEnabled = manager.isErrorLimitEnabled(),
            errorLimit = manager.getErrorLimit(),
            hintLimitEnabled = manager.isHintLimitEnabled(),
            hintLimit = manager.getHintLimit(),
            digitCountEnabled = manager.isDigitCountEnabled(),
        )
    )
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    fun setTimerEnabled(v: Boolean) {
        manager.setTimerEnabled(v)
        _settings.value = _settings.value.copy(timerEnabled = v)
    }

    fun setErrorLimitEnabled(v: Boolean) {
        manager.setErrorLimitEnabled(v)
        _settings.value = _settings.value.copy(errorLimitEnabled = v)
    }

    fun setErrorLimit(v: Int) {
        val clamped = v.coerceAtLeast(1)
        manager.setErrorLimit(clamped)
        _settings.value = _settings.value.copy(errorLimit = clamped)
    }

    fun setHintLimitEnabled(v: Boolean) {
        manager.setHintLimitEnabled(v)
        _settings.value = _settings.value.copy(hintLimitEnabled = v)
    }

    fun setHintLimit(v: Int) {
        val clamped = v.coerceAtLeast(1)
        manager.setHintLimit(clamped)
        _settings.value = _settings.value.copy(hintLimit = clamped)
    }

    fun setDigitCountEnabled(v: Boolean) {
        manager.setDigitCountEnabled(v)
        _settings.value = _settings.value.copy(digitCountEnabled = v)
    }
}
