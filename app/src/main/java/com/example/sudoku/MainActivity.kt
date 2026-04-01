package com.example.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudoku.navigation.AppNavigation
import com.example.sudoku.ui.theme.SudokuTheme
import com.example.sudoku.viewmodel.InputPreferenceViewModel
import com.example.sudoku.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val inputPreferenceViewModel: InputPreferenceViewModel = viewModel()
            val currentTheme by themeViewModel.currentTheme.collectAsState()

            SudokuTheme(appColorTheme = currentTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        themeViewModel = themeViewModel,
                        inputPreferenceViewModel = inputPreferenceViewModel,
                    )
                }
            }
        }
    }
}
