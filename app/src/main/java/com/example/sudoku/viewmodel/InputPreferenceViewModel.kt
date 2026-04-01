package com.example.sudoku.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sudoku.data.InputPreferenceManager
import com.example.sudoku.model.InputPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InputPreferenceViewModel(application: Application) : AndroidViewModel(application) {
    private val manager = InputPreferenceManager(application)
    private val _preference = MutableStateFlow(manager.getPreference())
    val preference: StateFlow<InputPreference> = _preference.asStateFlow()

    fun setPreference(pref: InputPreference) {
        manager.savePreference(pref)
        _preference.value = pref
    }
}
