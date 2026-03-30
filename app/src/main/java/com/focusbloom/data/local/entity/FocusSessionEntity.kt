// Additional Entity files for FocusBloom
package com.focusbloom.data.local.entity

import androidx.room.*

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String? = null,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long? = null,
    @ColumnInfo(name = "duration") val duration: Long = 0,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "interruptions") val interruptions: Int = 0,
    @ColumnInfo(name = "mode") val mode: String = "POMODORO",
    @ColumnInfo(name = "pomodoros_completed") val pomodorosCompleted: Int = 0,
    @ColumnInfo(name = "break_duration") val breakDuration: Long = 0
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "tasks_completed") val tasksCompleted: Int = 0,
    @ColumnInfo(name = "tasks_created") val tasksCreated: Int = 0,
    @ColumnInfo(name = "focus_time_minutes") val focusTimeMinutes: Int = 0,
    @ColumnInfo(name = "pomodoros_completed") val pomodorosCompleted: Int = 0,
    @ColumnInfo(name = "habits_checked") val habitsChecked: Int = 0,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)