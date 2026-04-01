package com.example.sudoku.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sudoku.model.Difficulty
import com.example.sudoku.ui.screens.EndScreen
import com.example.sudoku.ui.screens.GameScreen
import com.example.sudoku.ui.screens.SettingsScreen
import com.example.sudoku.ui.screens.SolverScreen
import com.example.sudoku.ui.screens.StartScreen
import com.example.sudoku.ui.screens.StatisticsScreen
import com.example.sudoku.viewmodel.GameViewModel
import com.example.sudoku.viewmodel.InputPreferenceViewModel
import com.example.sudoku.viewmodel.SolverViewModel
import com.example.sudoku.viewmodel.ThemeViewModel

object Routes {
    const val START = "start"
    const val GAME = "game/{difficulty}?loadSaved={loadSaved}"
    const val END = "end/{difficulty}"
    const val SOLVER = "solver"
    const val STATISTICS = "statistics"
    const val SETTINGS = "settings"

    fun game(difficulty: Difficulty) = "game/${difficulty.name}?loadSaved=false"
    fun continueGame() = "game/SAVED?loadSaved=true"
    fun end(difficulty: Difficulty) = "end/${difficulty.name}"
}

@Composable
fun AppNavigation(
    themeViewModel: ThemeViewModel,
    inputPreferenceViewModel: InputPreferenceViewModel,
) {
    val navController = rememberNavController()
    val viewModel: GameViewModel = viewModel()
    val solverViewModel: SolverViewModel = viewModel()
    val inputPreference by inputPreferenceViewModel.preference.collectAsState()

    NavHost(navController = navController, startDestination = Routes.START) {

        composable(Routes.START) {
            StartScreen(
                onDifficultySelected = { difficulty ->
                    viewModel.startGame(difficulty)
                    navController.navigate(Routes.game(difficulty))
                },
                onContinueGame = {
                    navController.navigate(Routes.continueGame())
                },
                onSolverSelected = {
                    navController.navigate(Routes.SOLVER)
                },
                onStatisticsSelected = {
                    navController.navigate(Routes.STATISTICS)
                },
                onSettingsSelected = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = Routes.GAME,
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("loadSaved") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val loadSaved = backStackEntry.arguments?.getBoolean("loadSaved") ?: false

            LaunchedEffect(loadSaved) {
                if (loadSaved) {
                    viewModel.loadSavedGame()
                }
            }

            GameScreen(
                viewModel = viewModel,
                inputPreference = inputPreference,
                onGameComplete = {
                    val currentDifficulty = viewModel.state.value.difficulty
                    navController.navigate(Routes.end(currentDifficulty)) {
                        popUpTo(Routes.START)
                    }
                },
                onBack = {
                    navController.navigate(Routes.START) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.END,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficultyName = backStackEntry.arguments?.getString("difficulty") ?: Difficulty.EASY.name
            val difficulty = try { Difficulty.valueOf(difficultyName) } catch (e: IllegalArgumentException) { Difficulty.EASY }
            EndScreen(
                onPlayAgain = {
                    viewModel.startGame(difficulty)
                    navController.navigate(Routes.game(difficulty)) {
                        popUpTo(Routes.START)
                    }
                },
                onMenu = {
                    navController.navigate(Routes.START) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SOLVER) {
            SolverScreen(
                viewModel = solverViewModel,
                onBack = {
                    navController.navigate(Routes.START) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.STATISTICS) {
            StatisticsScreen(
                onHomeSelected = {
                    navController.navigate(Routes.START) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                },
                onSettingsSelected = {
                    navController.navigate(Routes.SETTINGS) {
                        popUpTo(Routes.START)
                    }
                }
            )
        }

        composable(Routes.SETTINGS) {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            SettingsScreen(
                currentTheme = currentTheme,
                onThemeSelected = { themeViewModel.setTheme(it) },
                currentInputPreference = inputPreference,
                onInputPreferenceSelected = { inputPreferenceViewModel.setPreference(it) },
                onHomeSelected = {
                    navController.navigate(Routes.START) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                },
                onStatisticsSelected = {
                    navController.navigate(Routes.STATISTICS) {
                        popUpTo(Routes.START)
                    }
                }
            )
        }
    }
}
