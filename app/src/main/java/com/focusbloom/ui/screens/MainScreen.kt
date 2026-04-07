package com.focusbloom.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.focusbloom.ui.screens.task.TaskScreen
import com.focusbloom.ui.screens.timer.TimerScreen
import com.focusbloom.ui.screens.statistics.StatisticsScreen
import com.focusbloom.ui.screens.settings.SettingsScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Task : BottomNavItem("task", "任务", Icons.AutoMirrored.Filled.List)
    data object Timer : BottomNavItem("timer", "专注", Icons.Default.Timer)
    data object Statistics : BottomNavItem("statistics", "统计", Icons.Default.ShowChart)
    data object Settings : BottomNavItem("settings", "设置", Icons.Default.Settings)
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavItem.Task,
        BottomNavItem.Timer,
        BottomNavItem.Statistics,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> TaskScreen()
                1 -> TimerScreen()
                2 -> StatisticsScreen()
                3 -> SettingsScreen()
            }
        }
    }
}
