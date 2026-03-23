package com.example.sudoku.game

object SudokuSolver {

    // Проверяет, можно ли поставить num в позицию (row, col)
    fun isValid(board: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Проверка строки
        for (c in 0..8) {
            if (board[row][c] == num) return false
        }
        // Проверка столбца
        for (r in 0..8) {
            if (board[r][col] == num) return false
        }
        // Проверка блока 3×3
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3
        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (board[r][c] == num) return false
            }
        }
        return true
    }

    // Решает доску на месте. Возвращает true если решение найдено.
    fun solve(board: Array<IntArray>): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    for (num in 1..9) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            if (solve(board)) return true
                            board[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    // Считает количество решений (останавливается после 2 — для проверки уникальности)
    fun countSolutions(board: Array<IntArray>, limit: Int = 2): Int {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board[row][col] == 0) {
                    var count = 0
                    for (num in 1..9) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num
                            count += countSolutions(board, limit)
                            board[row][col] = 0
                            if (count >= limit) return count
                        }
                    }
                    return count
                }
            }
        }
        return 1 // Все клетки заполнены — одно решение найдено
    }
}
