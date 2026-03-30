package com.focusbloom.service

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.os.*
import androidx.core.app.NotificationCompat
import com.focusbloom.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

/**
 * Foreground service for timer - ensures timer runs even when app is backgrounded
 */
@AndroidEntryPoint
class TimerService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null
    
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    private val vibrator by lazy { getSystemService(VIBRATOR_SERVICE) as Vibrator }

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val duration = intent.getLongExtra(EXTRA_DURATION, 25 * 60 * 1000L)
                val mode = intent.getStringExtra(EXTRA_MODE) ?: TimerMode.POMODORO.name
                startTimer(duration, TimerMode.valueOf(mode))
            }
            ACTION_PAUSE_TIMER -> pauseTimer()
            ACTION_RESUME_TIMER -> resumeTimer()
            ACTION_STOP_TIMER -> stopTimer()
            ACTION_ADD_MINUTE -> addMinute()
        }
        return START_STICKY
    }

    private fun startTimer(duration: Long, mode: TimerMode) {
        if (timerJob?.isActive == true) return

        val startTime = System.currentTimeMillis()
        _timerState.value = TimerState(
            isRunning = true,
            isPaused = false,
            startTime = startTime,
            totalDuration = duration,
            remainingTime = duration,
            mode = mode
        )

        startForegroundService()

        timerJob = serviceScope.launch {
            while (isActive && _timerState.value.remainingTime > 0) {
                delay(1000)
                val state = _timerState.value
                val newRemaining = state.remainingTime - 1000
                _timerState.value = state.copy(remainingTime = maxOf(0, newRemaining))
                
                if (newRemaining <= 0) {
                    onTimerComplete()
                } else {
                    updateNotification()
                }
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false, isPaused = true)
        updateNotification()
    }

    private fun resumeTimer() {
        val state = _timerState.value
        startTimer(state.remainingTime, state.mode)
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun addMinute() {
        val state = _timerState.value
        _timerState.value = state.copy(remainingTime = state.remainingTime + 60000)
    }

    private fun onTimerComplete() {
        val state = _timerState.value
        _timerState.value = state.copy(isRunning = false, isCompleted = true)
        
        // Vibration
        vibrateDevice()
        
        // Update notification
        val notification = buildNotification(
            title = getString(R.string.timer_complete),
            content = getString(R.string.timer_complete_desc, state.mode.displayName)
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        // Auto-start break if enabled (implement later)
        // For now, just stop
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun vibrateDevice() {
        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun startForegroundService() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.timer_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.timer_channel_desc)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(
        title: String = getString(R.string.timer_running),
        content: String = formatTime(_timerState.value.remainingTime)
    ): android.app.Notification {
        val pauseIntent = PendingIntent.getService(
            this, 0,
            Intent(this, TimerService::class.java).setAction(ACTION_PAUSE_TIMER),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, TimerService::class.java).setAction(ACTION_STOP_TIMER),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val addMinuteIntent = PendingIntent.getService(
            this, 2,
            Intent(this, TimerService::class.java).setAction(ACTION_ADD_MINUTE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_timer)
            .setOngoing(true)
            .setSilent(true)
            .addAction(R.drawable.ic_add, "+1分", addMinuteIntent)
            .addAction(R.drawable.ic_pause, "暂停", pauseIntent)
            .addAction(R.drawable.ic_stop, "停止", stopIntent)
            .build()
    }

    private fun updateNotification() {
        if (_timerState.value.isRunning) {
            val notification = buildNotification()
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun formatTime(millis: Long): String {
        val minutes = millis / 60000
        val seconds = (millis % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1001
        
        const val ACTION_START_TIMER = "com.focusbloom.START_TIMER"
        const val ACTION_PAUSE_TIMER = "com.focusbloom.PAUSE_TIMER"
        const val ACTION_RESUME_TIMER = "com.focusbloom.RESUME_TIMER"
        const val ACTION_STOP_TIMER = "com.focusbloom.STOP_TIMER"
        const val ACTION_ADD_MINUTE = "com.focusbloom.ADD_MINUTE"
        
        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_MODE = "extra_mode"
    }
}

data class TimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val startTime: Long = 0,
    val totalDuration: Long = 25 * 60 * 1000, // Default 25 minutes
    val remainingTime: Long = 25 * 60 * 1000,
    val mode: TimerMode = TimerMode.POMODORO
)

enum class TimerMode(val displayName: String) {
    POMODORO("番茄钟"),
    STOPWATCH("正计时"),
    COUNTDOWN("倒计时")
}