package com.example.sudoku.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sudoku.model.Difficulty
import com.example.sudoku.ui.screens.EndScreen
import com.example.sudoku.ui.screens.GameScreen
import com.example.sudoku.ui.screens.SolverScreen
import com.example.sudoku.ui.screens.StartScreen
import com.example.sudoku.viewmodel.GameViewModel
import com.example.sudoku.viewmodel.SolverViewModel

object Routes {
    const val START = "start"
    const val GAME = "game/{difficulty}"
    const val END = "end/{difficulty}"
    const val SOLVER = "solver"

    fun game(difficulty: Difficulty) = "game/${difficulty.name}"
    fun end(difficulty: Difficulty) = "end/${difficulty.name}"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: GameViewModel = viewModel()
    val solverViewModel: SolverViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.START) {

        composable(Routes.START) {
            StartScreen(
                onDifficultySelected = { difficulty ->
                    viewModel.startGame(difficulty)
                    navController.navigate(Routes.game(difficulty))
                },
                onSolverSelected = {
                    navController.navigate(Routes.SOLVER)
                }
            )
        }

        composable(
            route = Routes.GAME,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficultyName = backStackEntry.arguments?.getString("difficulty") ?: Difficulty.EASY.name
            val difficulty = Difficulty.valueOf(difficultyName)
            GameScreen(
                viewModel = viewModel,
                onGameComplete = {
                    navController.navigate(Routes.end(difficulty)) {
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
            val difficulty = Difficulty.valueOf(difficultyName)
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
    }
}
