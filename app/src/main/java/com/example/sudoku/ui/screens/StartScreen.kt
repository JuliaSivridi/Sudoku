package com.example.sudoku.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.data.GameSaveManager
import com.example.sudoku.model.Difficulty

@Composable
fun BottomNavBar(
    currentTab: String,
    onHomeSelected: () -> Unit,
    onStatisticsSelected: () -> Unit,
    onSettingsSelected: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentTab == "home",
            onClick = onHomeSelected,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentTab == "stats",
            onClick = onStatisticsSelected,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Leaderboard,
                    contentDescription = "Stats"
                )
            },
            label = { Text("Stats") }
        )
        NavigationBarItem(
            selected = currentTab == "settings",
            onClick = onSettingsSelected,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") }
        )
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StartScreen(
    onDifficultySelected: (Difficulty) -> Unit,
    onContinueGame: () -> Unit,
    onSolverSelected: () -> Unit,
    onStatisticsSelected: () -> Unit,
    onSettingsSelected: () -> Unit,
) {
    val context = LocalContext.current
    var hasSavedGame by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasSavedGame = GameSaveManager(context).hasSavedGame()
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentTab = "home",
                onHomeSelected = {},
                onStatisticsSelected = onStatisticsSelected,
                onSettingsSelected = onSettingsSelected,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SUDOKU",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select difficulty",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            MenuButton(
                text = Difficulty.EASY.label,
                icon = Icons.Outlined.ChildCare,
                onClick = { onDifficultySelected(Difficulty.EASY) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = Difficulty.MEDIUM.label,
                icon = Icons.Outlined.SentimentSatisfied,
                onClick = { onDifficultySelected(Difficulty.MEDIUM) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = Difficulty.HARD.label,
                icon = Icons.Outlined.Psychology,
                onClick = { onDifficultySelected(Difficulty.HARD) }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
            )

            if (hasSavedGame) {
                MenuButton(
                    text = "Continue",
                    icon = Icons.Outlined.PlayArrow,
                    onClick = onContinueGame
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                )
            }

            MenuButton(
                text = "Solver",
                icon = Icons.Outlined.EditNote,
                onClick = onSolverSelected
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
