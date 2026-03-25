package com.example.sudoku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

@Composable
fun SudokuTheme(
    appColorTheme: AppColorTheme = AppColorTheme.ORANGE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeColors = appColorTheme.themeColors()

    val colorScheme = if (darkTheme) darkColorScheme(
        primary = themeColors.accent,
        onPrimary = Color.Black,
        secondary = themeColors.accentVariant,
        onSecondary = Color.Black,
        background = BackgroundDark,
        surface = SurfaceDark,
        onBackground = OnBackgroundDark,
        onSurface = OnBackgroundDark,
    ) else lightColorScheme(
        primary = themeColors.accent,
        onPrimary = Color.White,
        secondary = themeColors.accentVariant,
        onSecondary = Color.White,
        background = BackgroundLight,
        surface = SurfaceLight,
        onBackground = OnBackgroundLight,
        onSurface = OnBackgroundLight,
    )

    CompositionLocalProvider(LocalAppThemeColors provides themeColors) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
