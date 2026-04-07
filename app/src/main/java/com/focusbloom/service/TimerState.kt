package com.focusbloom.service

data class TimerState(
    val mode: TimerMode = TimerMode.POMODORO,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val remainingTime: Long = TimerMode.POMODORO.durationMinutes * 60 * 1000L,
    val totalDuration: Long = TimerMode.POMODORO.durationMinutes * 60 * 1000L
)