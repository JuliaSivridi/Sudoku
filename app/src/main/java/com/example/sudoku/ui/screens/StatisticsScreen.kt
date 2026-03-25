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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.data.StatsManager
import com.example.sudoku.model.Difficulty

@Composable
fun StatisticsScreen(
    onHomeSelected: () -> Unit,
    onSettingsSelected: () -> Unit,
) {
    val context = LocalContext.current
    val statsManager = remember { StatsManager(context) }
    val counts = remember { Difficulty.entries.associateWith { statsManager.getCount(it) } }

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

            Text(
                text = "Games Played",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Mode",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Completed",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Difficulty.entries.forEachIndexed { index, difficulty ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = difficulty.label,
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${counts[difficulty] ?: 0}",
                            modifier = Modifier.weight(1f),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
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
