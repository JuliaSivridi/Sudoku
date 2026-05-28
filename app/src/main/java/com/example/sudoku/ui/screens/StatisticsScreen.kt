package com.example.sudoku.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.data.StatsManager
import com.example.sudoku.model.Difficulty

@Composable
fun StatisticsScreen(
    timerEnabled: Boolean,
    onHomeSelected: () -> Unit,
    onSettingsSelected: () -> Unit,
) {
    val context = LocalContext.current
    val statsManager = remember { StatsManager(context) }
    val counts = remember { Difficulty.entries.associateWith { statsManager.getCount(it) } }
    val bestTimes = remember { Difficulty.entries.associateWith { statsManager.getBestTime(it) } }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentTab = "stats",
                onHomeSelected = onHomeSelected,
                onStatisticsSelected = {},
                onSettingsSelected = onSettingsSelected,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Statistics",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                // Заголовок
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mode",
                        modifier = Modifier.weight(1.4f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    // Иконка «сыграно»
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Played",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                    if (timerEnabled) {
                        // Иконка «лучшее время»
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = "Best time",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Difficulty.entries.forEachIndexed { index, difficulty ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = difficulty.label,
                            modifier = Modifier.weight(1.4f),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${counts[difficulty] ?: 0}",
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (timerEnabled) {
                            val best = bestTimes[difficulty]
                            Text(
                                text = if (best != null) formatTime(best.toInt()) else "—",
                                modifier = Modifier.weight(1f),
                                fontSize = 15.sp,
                                fontFamily = if (best != null) FontFamily.Monospace else FontFamily.Default,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    if (index < Difficulty.entries.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}
