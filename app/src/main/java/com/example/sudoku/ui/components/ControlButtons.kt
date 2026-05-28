package com.example.sudoku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.InputMode

@Composable
fun ControlButtons(
    inputMode: InputMode,
    isCellFirst: Boolean = false,
    isAutoNotesActive: Boolean = false,
    isEnabled: Boolean = true,
    // -1 = безлимитно, 0 = исчерпаны, >0 = осталось N
    hintsRemaining: Int = -1,
    onUndo: () -> Unit,
    onToggleErase: () -> Unit,
    onToggleNotes: () -> Unit,
    onToggleAutoNotes: () -> Unit,
    onHint: () -> Unit
) {
    val hintBadge: String? = if (hintsRemaining > 0) hintsRemaining.toString() else null
    val hintDisabled = hintsRemaining == 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.35f),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ControlButton(
            icon = Icons.AutoMirrored.Filled.Undo,
            label = "Undo",
            isActive = false,
            onClick = onUndo,
            modifier = Modifier.weight(1f)
        )
        ControlButton(
            icon = Icons.Filled.Close,
            label = "Clear",
            isActive = !isCellFirst && inputMode == InputMode.ERASE,
            onClick = onToggleErase,
            modifier = Modifier.weight(1f)
        )
        ControlButton(
            icon = Icons.Filled.EditNote,
            label = "Notes",
            isActive = inputMode == InputMode.NOTES,
            onClick = onToggleNotes,
            modifier = Modifier.weight(1f)
        )
        ControlButton(
            icon = Icons.Outlined.AutoAwesome,
            label = "Clues",
            isActive = isAutoNotesActive,
            onClick = onToggleAutoNotes,
            modifier = Modifier.weight(1f)
        )
        ControlButton(
            icon = Icons.Outlined.Lightbulb,
            label = "Hint",
            isActive = false,
            badgeText = hintBadge,
            isButtonDisabled = hintDisabled,
            onClick = onHint,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    badgeText: String? = null,
    isButtonDisabled: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = when {
        isButtonDisabled -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
        isActive -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onBackground
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (!isButtonDisabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Иконка с опциональным счётчиком в правом верхнем углу
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
            if (badgeText != null) {
                Text(
                    text = badgeText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tint.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = tint
        )
    }
}
