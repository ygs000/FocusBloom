package com.focusbloom.service

enum class TimerMode(val displayName: String, val durationMinutes: Int) {
    POMODORO("专注", 25),
    SHORT_BREAK("短休", 5),
    LONG_BREAK("长休", 15)
}