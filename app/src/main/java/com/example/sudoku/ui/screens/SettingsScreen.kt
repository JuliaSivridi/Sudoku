package com.example.sudoku.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.InputPreference
import com.example.sudoku.ui.theme.AppColorTheme
import com.example.sudoku.ui.theme.LocalAppThemeColors
import com.example.sudoku.ui.theme.themeColors
import com.example.sudoku.viewmodel.GameSettings

@Composable
fun SettingsScreen(
    currentTheme: AppColorTheme,
    onThemeSelected: (AppColorTheme) -> Unit,
    currentInputPreference: InputPreference,
    onInputPreferenceSelected: (InputPreference) -> Unit,
    gameSettings: GameSettings,
    onTimerEnabledChanged: (Boolean) -> Unit,
    onErrorLimitEnabledChanged: (Boolean) -> Unit,
    onErrorLimitChanged: (Int) -> Unit,
    onHintLimitEnabledChanged: (Boolean) -> Unit,
    onHintLimitChanged: (Int) -> Unit,
    onDigitCountEnabledChanged: (Boolean) -> Unit,
    onHomeSelected: () -> Unit,
    onStatisticsSelected: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentTab = "settings",
                onHomeSelected = onHomeSelected,
                onStatisticsSelected = onStatisticsSelected,
                onSettingsSelected = {},
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Режим ввода ────────────────────────────────────────────────────────
            SectionLabel("Input mode")
            Spacer(modifier = Modifier.height(12.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = currentInputPreference == InputPreference.NUMBER_FIRST,
                    onClick = { onInputPreferenceSelected(InputPreference.NUMBER_FIRST) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Number first", fontSize = 15.sp)
                }
                SegmentedButton(
                    selected = currentInputPreference == InputPreference.CELL_FIRST,
                    onClick = { onInputPreferenceSelected(InputPreference.CELL_FIRST) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Cell first", fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Геймплей ──────────────────────────────────────────────────────────
            SectionLabel("Gameplay")
            Spacer(modifier = Modifier.height(12.dp))

            SettingCheckboxRow(
                checked = gameSettings.timerEnabled,
                onCheckedChange = onTimerEnabledChanged,
                title = "Timer",
                description = "Show elapsed time and pause button during the game"
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingCheckboxRow(
                checked = gameSettings.errorLimitEnabled,
                onCheckedChange = onErrorLimitEnabledChanged,
                title = "Error limit",
                description = "End the game after a set number of mistakes",
                stepperValue = gameSettings.errorLimit,
                onStepperValueChange = onErrorLimitChanged,
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingCheckboxRow(
                checked = gameSettings.hintLimitEnabled,
                onCheckedChange = onHintLimitEnabledChanged,
                title = "Hint limit",
                description = "Restrict the number of hints available per game",
                stepperValue = gameSettings.hintLimit,
                onStepperValueChange = onHintLimitChanged,
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingCheckboxRow(
                checked = gameSettings.digitCountEnabled,
                onCheckedChange = onDigitCountEnabledChanged,
                title = "Digit count",
                description = "Show how many of each digit remain to be placed"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Тема ──────────────────────────────────────────────────────────────
            SectionLabel("Theme")
            Spacer(modifier = Modifier.height(12.dp))

            for (theme in AppColorTheme.values()) {
                Button(
                    onClick = { onThemeSelected(theme) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.themeColors().accent,
                        contentColor = Color.White
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (currentTheme == theme) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterStart)
                            )
                        }
                        Text(
                            text = theme.displayName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SettingCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    description: String,
    stepperValue: Int? = null,
    onStepperValueChange: ((Int) -> Unit)? = null,
) {
    val appColors = LocalAppThemeColors.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = appColors.accent,
                    checkmarkColor = Color.White
                )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )
            }
            if (stepperValue != null && onStepperValueChange != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Stepper(
                    value = stepperValue,
                    onValueChange = onStepperValueChange,
                    enabled = checked,
                )
            }
        }
    }
}

// Stepper: кнопки − / + без клавиатуры
@Composable
private fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    enabled: Boolean,
    min: Int = 1,
    max: Int = 20,
) {
    val activeColor = MaterialTheme.colorScheme.onSurface
    val dimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)

    Row(verticalAlignment = Alignment.CenterVertically) {
        // Кнопка «−»
        StepButton(
            icon = Icons.Filled.Remove,
            contentDescription = "Decrease",
            enabled = enabled && value > min,
            activeColor = activeColor,
            dimColor = dimColor,
            onClick = { onValueChange(value - 1) }
        )

        // Текущее значение
        Text(
            text = value.toString(),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = if (enabled) activeColor else dimColor,
            modifier = Modifier.width(30.dp)
        )

        // Кнопка «+»
        StepButton(
            icon = Icons.Filled.Add,
            contentDescription = "Increase",
            enabled = enabled && value < max,
            activeColor = activeColor,
            dimColor = dimColor,
            onClick = { onValueChange(value + 1) }
        )
    }
}

@Composable
private fun StepButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    dimColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) activeColor else dimColor,
            modifier = Modifier.size(20.dp)
        )
    }
}
