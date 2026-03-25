package com.example.sudoku.ui.theme

import androidx.compose.ui.graphics.Color

// Accent — мягкий тёплый оранжевый
val Orange = Color(0xFFCF7A30)
val OrangeLight = Color(0xFFE59A52)

// Light theme
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF1C1C1C)

// Dark theme
val BackgroundDark = Color(0xFF212121)
val SurfaceDark = Color(0xFF2C2C2C)
val OnBackgroundDark = Color(0xFFEEEEEE)

// Grid borders — светлая тема
val GridOuterBorderLight = Color(0xFF4D5666)   // внешняя рамка и границы блоков 3×3
val GridInnerBorderLight = Color(0xFFBEC5D1)   // внутренние линии между ячейками

// Grid borders — тёмная тема
val GridOuterBorderDark = Color(0xFF656D7A)    // внешняя рамка и границы блоков 3×3
val GridInnerBorderDark = Color(0xFF363C47)    // внутренние линии между ячейками

// Cell highlights — мягкие, ненавязчивые, чисто серые
val CellHighlightLight = Color(0xFFF0F0F0)   // строка/столбец, светлая тема
val CellHighlightDark = Color(0xFF363636)    // строка/столбец, тёмная тема — чистый тёмный серый
val CellSelectedLight = Color(0xFFE4E4E4)    // выбранная ячейка, светлая
val CellSelectedDark = Color(0xFF424242)     // выбранная ячейка, тёмная
// Digit highlight — одинаковые ячейки с выбранной цифрой
val CellDigitHighlightLight = Color(0x33CF7A30)  // ~20% alpha Orange, светлая тема
val CellDigitHighlightDark = Color(0x80CF7A30)   // 50% alpha Orange, тёмная тема

// Number colors
val GivenNumberLight = Color(0xFF1C1C1C)     // системные цифры, светлая тема
val GivenNumberDark = Color(0xFFEEEEEE)      // системные цифры, тёмная тема
val UserNumberLight = Color(0xFFCF7A30)      // пользовательские цифры, светлая тема — оранжевый
val UserNumberDark = Color(0xFFE59A52)       // пользовательские цифры, тёмная тема — светлый оранжевый
val NoteNumberLight = Color(0xFF909090)      // заметки, светлая тема
val NoteNumberDark = Color(0xFF9E9E9E)       // заметки, тёмная тема

// Conflict highlight colors
val ConflictColorLight = Color(0xFFCC1515)   // яркий красный, светлая тема
val ConflictColorDark = Color(0xFFFF5252)    // яркий красный, тёмная тема
val ConflictBgLight = Color(0x33CC1515)      // ~20% alpha красный фон, светлая тема
val ConflictBgDark  = Color(0x80CC1515)      // ~50% alpha красный фон, тёмная тема
