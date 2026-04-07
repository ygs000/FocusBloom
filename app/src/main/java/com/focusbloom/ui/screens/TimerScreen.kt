package com.focusbloom.ui.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusbloom.service.TimerMode
import com.focusbloom.service.TimerService
import com.focusbloom.service.TimerState
import kotlinx.coroutines.delay

@Composable
fun FocusTimerScreen() {
    val context = LocalContext.current
    var timerService by remember { mutableStateOf<TimerService?>(null) }
    var isBound by remember { mutableStateOf(false) }
    
    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TimerService.LocalBinder
                timerService = binder.getService()
                isBound = true
            }
            
            override fun onServiceDisconnected(name: ComponentName?) {
                timerService = null
                isBound = false
            }
        }
    }
    
    DisposableEffect(context) {
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        
        onDispose {
            if (isBound) {
                context.unbindService(serviceConnection)
            }
        }
    }
    
    val timerState by timerService?.timerState?.collectAsState() 
        ?: remember { mutableStateOf(TimerState()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode selector
        ModeSelector(
            selectedMode = timerState.mode,
            onModeSelected = { /* Handle mode change */ },
            enabled = !timerState.isRunning
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Timer display
        TimerDisplay(
            remainingTime = timerState.remainingTime,
            totalDuration = timerState.totalDuration,
            isRunning = timerState.isRunning
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Control buttons
        TimerControls(
            isRunning = timerState.isRunning,
            isPaused = timerState.isPaused,
            onStart = { 
                val intent = Intent(context, TimerService::class.java).apply {
                    action = TimerService.ACTION_START_TIMER
                    putExtra(TimerService.EXTRA_DURATION, 25 * 60 * 1000L)
                    putExtra(TimerService.EXTRA_MODE, TimerMode.POMODORO.name)
                }
                context.startService(intent)
            },
            onPause = {
                val intent = Intent(context, TimerService::class.java).apply {
                    action = TimerService.ACTION_PAUSE_TIMER
                }
                context.startService(intent)
            },
            onResume = {
                val intent = Intent(context, TimerService::class.java).apply {
                    action = TimerService.ACTION_RESUME_TIMER
                }
                context.startService(intent)
            },
            onStop = {
                val intent = Intent(context, TimerService::class.java).apply {
                    action = TimerService.ACTION_STOP_TIMER
                }
                context.startService(intent)
            }
        )
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
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimerMode.values().forEach { mode ->
            val isSelected = mode == selectedMode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary 
                        else Color.Transparent
                    )
                    .clickable(enabled = enabled) { onModeSelected(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                           else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun TimerDisplay(
    remainingTime: Long,
    totalDuration: Long,
    isRunning: Boolean
) {
    val progress = if (totalDuration > 0) {
        (totalDuration - remainingTime).toFloat() / totalDuration.toFloat()
    } else 0f
    
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Progress ring
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 12.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = if (isRunning) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.outline,
            strokeWidth = 12.dp,
            trackColor = Color.Transparent
        )
        
        // Time display
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val minutes = remainingTime / 60000
            val seconds = (remainingTime % 60000) / 1000
            
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (!isRunning && remainingTime < totalDuration) {
                Text(
                    text = "已暂停",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun TimerControls(
    isRunning: Boolean,
    isPaused: Boolean,
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
        // Stop button (only show when running or paused)
        if (isRunning || isPaused) {
            FilledIconButton(
                onClick = onStop,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "停止",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        // Main control button (Start / Pause / Resume)
        val mainButtonColors = when {
            isRunning -> ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            isPaused -> ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
            else -> ButtonDefaults.filledTonalButtonColors()
        }
        
        val mainAction = when {
            isRunning -> onPause
            isPaused -> onResume
            else -> onStart
        }
        
        val mainIcon = when {
            isRunning -> Icons.Default.Pause
            isPaused -> Icons.Default.PlayArrow
            else -> Icons.Default.PlayArrow
        }
        
        val mainText = when {
            isRunning -> "暂停"
            isPaused -> "继续"
            else -> "开始专注"
        }
        
        FilledTonalButton(
            onClick = mainAction,
            modifier = Modifier
                .height(64.dp)
                .widthIn(min = 160.dp),
            colors = mainButtonColors
        ) {
            Icon(
                imageVector = mainIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = mainText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

