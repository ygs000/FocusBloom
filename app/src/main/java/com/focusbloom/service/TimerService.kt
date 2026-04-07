package com.focusbloom.service

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.os.*
import androidx.core.app.NotificationCompat
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

    @Inject
    lateinit var coroutineScope: CoroutineScope

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationId = 1
    private val channelId = "timer_channel"

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> startTimer()
            ACTION_STOP_TIMER -> stopTimer()
            ACTION_PAUSE_TIMER -> pauseTimer()
            ACTION_RESUME_TIMER -> resumeTimer()
        }

        return START_STICKY
    }

    private fun startTimer() {
        startForeground(notificationId, createNotification("Timer running"))
    }

    private fun stopTimer() {
        stopSelf()
    }

    private fun pauseTimer() {
        // Implementation placeholder
    }

    private fun resumeTimer() {
        // Implementation placeholder
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows timer notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("FocusBloom Timer")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    companion object {
        const val ACTION_START_TIMER = "ACTION_START_TIMER"
        const val ACTION_STOP_TIMER = "ACTION_STOP_TIMER"
        const val ACTION_PAUSE_TIMER = "ACTION_PAUSE_TIMER"
        const val ACTION_RESUME_TIMER = "ACTION_RESUME_TIMER"
    }
}