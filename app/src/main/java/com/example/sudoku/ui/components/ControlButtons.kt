package com.example.sudoku.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.model.InputMode

@Composable
fun ControlButtons(
    inputMode: InputMode,
    onUndo: () -> Unit,
    onToggleErase: () -> Unit,
    onToggleNotes: () -> Unit,
    onAutoNotes: () -> Unit,
    onHint: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
            isActive = inputMode == InputMode.ERASE,
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
            icon = Icons.Outlined.AutoFixHigh,
            label = "Auto",
            isActive = false,
            onClick = onAutoNotes,
            modifier = Modifier.weight(1f)
        )
        ControlButton(
            icon = Icons.Outlined.Lightbulb,
            label = "Hint",
            isActive = false,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (isActive) MaterialTheme.colorScheme.primary
               else MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = tint
        )
    }
}
