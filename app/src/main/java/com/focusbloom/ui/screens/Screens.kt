package com.focusbloom.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.focusbloom.ui.theme.FocusBloomTheme

@Composable
fun TaskScreen(navController: NavController? = null) {
    FocusBloomTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("FocusBloom Tasks")
                Text("Work in progress...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun TimerScreen(navController: NavController? = null) {
    FocusBloomTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("FocusBloom Timer")
                Text("Work in progress...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun StatisticsScreen(navController: NavController? = null) {
    FocusBloomTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("FocusBloom Statistics")
                Text("Work in progress...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController? = null) {
    FocusBloomTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("FocusBloom Settings")
                Text("Work in progress...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}