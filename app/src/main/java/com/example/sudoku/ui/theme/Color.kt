package com.example.sudoku.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// Accent — мягкий тёплый оранжевый
val Orange = Color(0xFFE07E38)
val OrangeLight = Color(0xFFE8935A)

// Light theme
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF1C1C1C)

// Dark theme
val BackgroundDark = Color(0xFF1C1C1C)
val SurfaceDark = Color(0xFF363636)
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
val UserNumberLight = Color(0xFFE07E38)      // пользовательские цифры, светлая тема — оранжевый
val UserNumberDark = Color(0xFFE8935A)       // пользовательские цифры, тёмная тема — светлый оранжевый
val NoteNumberLight = Color(0xFF909090)      // заметки, светлая тема
val NoteNumberDark = Color(0xFF9E9E9E)       // заметки, тёмная тема

// Conflict highlight colors
val ConflictColorLight = Color(0xFFCC1515)   // яркий красный, светлая тема
val ConflictColorDark = Color(0xFFFF5252)    // яркий красный, тёмная тема
val ConflictBgLight = Color(0x33CC1515)      // ~20% alpha красный фон, светлая тема
val ConflictBgDark  = Color(0x80CC1515)      // ~50% alpha красный фон, тёмная тема

// ── App color themes ────────────────────────────────────────────────────────

enum class AppColorTheme(val displayName: String) {
    ORANGE("Orange"),
    GREEN("Green"),
    BLUE("Blue"),
    PURPLE("Purple")
}

data class AppThemeColors(
    val accent: Color,                       // primary / buttons / titles / selected digit text
    val accentVariant: Color,                // user digits in dark theme (lighter variant)
    val cellDigitHighlightLight: Color,      // ~20% alpha tint, light theme
    val cellDigitHighlightDark: Color,       // ~50% alpha tint, dark theme
    val userNumberLight: Color,              // user-placed digits, light theme
    val userNumberDark: Color,               // user-placed digits, dark theme
)

val OrangeThemeColors = AppThemeColors(
    accent                  = Color(0xFFE07E38),
    accentVariant           = Color(0xFFE8935A),
    cellDigitHighlightLight = Color(0x33CF7A30),
    cellDigitHighlightDark  = Color(0x80CF7A30),
    userNumberLight         = Color(0xFFE07E38),
    userNumberDark          = Color(0xFFE8935A),
)

val GreenThemeColors = AppThemeColors(
    accent                  = Color(0xFF4D8B53),
    accentVariant           = Color(0xFF79B57F),
    cellDigitHighlightLight = Color(0x334D8B53),
    cellDigitHighlightDark  = Color(0x804D8B53),
    userNumberLight         = Color(0xFF4D8B53),
    userNumberDark          = Color(0xFF79B57F),
)

val BlueThemeColors = AppThemeColors(
    accent                  = Color(0xFF3D73B0),
    accentVariant           = Color(0xFF6B9FD4),
    cellDigitHighlightLight = Color(0x333D73B0),
    cellDigitHighlightDark  = Color(0x803D73B0),
    userNumberLight         = Color(0xFF3D73B0),
    userNumberDark          = Color(0xFF6B9FD4),
)

val PurpleThemeColors = AppThemeColors(
    accent                  = Color(0xFF7A4FA3),
    accentVariant           = Color(0xFFA47BC8),
    cellDigitHighlightLight = Color(0x337A4FA3),
    cellDigitHighlightDark  = Color(0x807A4FA3),
    userNumberLight         = Color(0xFF7A4FA3),
    userNumberDark          = Color(0xFFA47BC8),
)

fun AppColorTheme.themeColors(): AppThemeColors = when (this) {
    AppColorTheme.ORANGE -> OrangeThemeColors
    AppColorTheme.GREEN  -> GreenThemeColors
    AppColorTheme.BLUE   -> BlueThemeColors
    AppColorTheme.PURPLE -> PurpleThemeColors
}

val LocalAppThemeColors = compositionLocalOf { OrangeThemeColors }
