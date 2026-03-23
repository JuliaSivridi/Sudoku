package com.example.sudoku.model

enum class Difficulty(val label: String, val givens: Int) {
    EASY("Easy", 46),
    MEDIUM("Medium", 36),
    HARD("Hard", 29)
}
