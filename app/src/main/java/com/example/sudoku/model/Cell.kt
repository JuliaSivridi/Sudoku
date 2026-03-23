package com.example.sudoku.model

data class Cell(
    val value: Int = 0,           // 0 = пусто
    val isGiven: Boolean = false, // true = системная цифра (нельзя менять)
    val notes: Set<Int> = emptySet() // цифры-заметки 1–9
)
