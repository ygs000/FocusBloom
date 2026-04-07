package com.focusbloom.ui.screens.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class TimerMode(val displayName: String, val defaultMinutes: Int) {
    POMODORO("番茄钟", 25),
    SHORT_BREAK("短休息", 5),
    LONG_BREAK("长休息", 15)
}

enum class TimerStatus { IDLE, RUNNING, PAUSED }

@Composable
fun TimerScreen() {
    var selectedMode by remember { mutableStateOf(TimerMode.POMODORO) }
    var timerStatus by remember { mutableStateOf(TimerStatus.IDLE) }
    var totalSeconds by remember { mutableStateOf(25 * 60) }
    var remainingSeconds by remember { mutableStateOf(25 * 60) }
    var completedPomodoros by remember { mutableIntStateOf(0) }

    // Reset when mode changes
    LaunchedEffect(selectedMode) {
        if (timerStatus == TimerStatus.IDLE) {
            totalSeconds = selectedMode.defaultMinutes * 60
            remainingSeconds = totalSeconds
        }
    }

    // Timer countdown
    LaunchedEffect(timerStatus) {
        if (timerStatus == TimerStatus.RUNNING) {
            while (remainingSeconds > 0 && timerStatus == TimerStatus.RUNNING) {
                delay(1000)
                remainingSeconds--
            }
            if (remainingSeconds == 0) {
                timerStatus = TimerStatus.IDLE
                if (selectedMode == TimerMode.POMODORO) {
                    completedPomodoros++
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "专注计时",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Mode selector
        ModeSelector(
            selectedMode = selectedMode,
            onModeSelected = { mode ->
                if (timerStatus == TimerStatus.IDLE) {
                    selectedMode = mode
                }
            },
            enabled = timerStatus == TimerStatus.IDLE
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Timer circle
        TimerCircle(
            remainingSeconds = remainingSeconds,
            totalSeconds = totalSeconds,
            isRunning = timerStatus == TimerStatus.RUNNING
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Control buttons
        TimerControls(
            timerStatus = timerStatus,
            onStart = {
                if (timerStatus == TimerStatus.IDLE) {
                    totalSeconds = selectedMode.defaultMinutes * 60
                    remainingSeconds = totalSeconds
                }
                timerStatus = TimerStatus.RUNNING
            },
            onPause = { timerStatus = TimerStatus.PAUSED },
            onResume = { timerStatus = TimerStatus.RUNNING },
            onStop = {
                timerStatus = TimerStatus.IDLE
                remainingSeconds = selectedMode.defaultMinutes * 60
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Completed count
        if (completedPomodoros > 0) {
            Text(
                text = "今日已完成 $completedPomodoros 个番茄",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ModeSelector(
    selectedMode: TimerMode,
    onModeSelected: (TimerMode) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
    ) {
        TimerMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode
            Surface(
                onClick = { if (enabled) onModeSelected(mode) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                enabled = enabled
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = mode.displayName,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            if (mode != TimerMode.entries.last()) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun TimerCircle(
    remainingSeconds: Long,
    totalSeconds: Long,
    isRunning: Boolean
) {
    val progress = if (totalSeconds > 0) {
        (totalSeconds - remainingSeconds).toFloat() / totalSeconds.toFloat()
    } else 0f

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(
                (size.width - diameter) / 2,
                (size.height - diameter) / 2
            )

            // Background circle
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isRunning) {
                Text(
                    text = "专注中...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (remainingSeconds == 0L) {
                Text(
                    text = "完成！",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TimerControls(
    timerStatus: TimerStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Stop button
        if (timerStatus != TimerStatus.IDLE) {
            FilledIconButton(
                onClick = onStop,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text("■", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }

        // Main action button
        val buttonText = when (timerStatus) {
            TimerStatus.IDLE -> "开始"
            TimerStatus.RUNNING -> "暂停"
            TimerStatus.PAUSED -> "继续"
        }

        Button(
            onClick = {
                when (timerStatus) {
                    TimerStatus.IDLE -> onStart()
                    TimerStatus.RUNNING -> onPause()
                    TimerStatus.PAUSED -> onResume()
                }
            },
            modifier = Modifier
                .height(56.dp)
                .widthIn(min = 140.dp)
        ) {
            Text(buttonText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
