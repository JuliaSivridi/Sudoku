package com.example.sudoku.game

import com.example.sudoku.model.Cell
import com.example.sudoku.model.Difficulty

object SudokuGenerator {

    // Генерирует полную валидную доску backtracking-ом с рандомизацией
    private fun generateFullBoard(): Array<IntArray> {
        val board = Array(9) { IntArray(9) }
        fillBoard(board)
        return board
    }

    private fun fillBoard(board: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    val nums = (1..9).shuffled()
                    for (num in nums) {
                        if (SudokuSolver.isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (fillBoard(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    // Генерирует головоломку: возвращает Pair(puzzle, solution)
    fun generate(difficulty: Difficulty): Pair<List<List<Cell>>, List<List<Int>>> {
        val solution = generateFullBoard()

        // Копируем решение для последующего удаления цифр
        val puzzle = Array(9) { r -> solution[r].copyOf() }

        val cellsToRemove = 81 - difficulty.givens
        removeCells(puzzle, cellsToRemove)

        val boardCells = List(9) { r ->
            List(9) { c ->
                if (puzzle[r][c] != 0) {
                    Cell(value = puzzle[r][c], isGiven = true)
                } else {
                    Cell()
                }
            }
        }

        val solutionList = List(9) { r -> List(9) { c -> solution[r][c] } }

        return Pair(boardCells, solutionList)
    }

    // Убирает клетки, сохраняя уникальность решения
    private fun removeCells(board: Array<IntArray>, count: Int) {
        val positions = (0..80).shuffled().toMutableList()
        var removed = 0

        for (pos in positions) {
            if (removed >= count) break
            val row = pos / 9
            val col = pos % 9
            if (board[row][col] == 0) continue

            val backup = board[row][col]
            board[row][col] = 0

            // Проверяем уникальность решения
            val copy = Array(9) { r -> board[r].copyOf() }
            if (SudokuSolver.countSolutions(copy) == 1) {
                removed++
            } else {
                // Решение не уникально — возвращаем цифру
                board[row][col] = backup
            }
        }
    }
}
