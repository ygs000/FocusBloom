// Domain layer - Entities
package com.focusbloom.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Task entity - Core domain model for tasks
 */
@Parcelize
data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val richContent: RichContent? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val reminders: List<Reminder> = emptyList(),
    val folderId: String? = null,
    val tags: List<String> = emptyList(),
    val parentTaskId: String? = null,
    val subTasks: List<Task> = emptyList(),
    val repeatRule: RepeatRule? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
    val focusSessions: List<FocusSession> = emptyList(),
    val estimatedPomodoros: Int = 0,
    val completedPomodoros: Int = 0
) : Parcelable

@Parcelize
enum class TaskStatus : Parcelable {
    TODO,
    IN_PROGRESS,
    DONE,
    EXPIRED,
    ARCHIVED
}

@Parcelize
enum class Priority : Parcelable {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    MINIMAL
}

@Parcelize
data class RichContent(
    val text: String,
    val images: List<String> = emptyList()
) : Parcelable

@Parcelize
data class Reminder(
    val id: String,
    val triggerTime: LocalDateTime,
    val type: ReminderType
) : Parcelable

@Parcelize
enum class ReminderType : Parcelable {
    BEFORE_1_DAY,
    BEFORE_1_HOUR,
    BEFORE_10_MIN,
    CUSTOM
}

@Parcelize
data class RepeatRule(
    val frequency: RepeatFrequency,
    val interval: Int = 1,
    val daysOfWeek: List<Int> = emptyList(),
    val endCondition: EndCondition = EndCondition.NEVER
) : Parcelable

@Parcelize
enum class RepeatFrequency : Parcelable {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    WEEKDAYS,
    CUSTOM
}

@Parcelize
sealed class EndCondition : Parcelable {
    @Parcelize
    data object NEVER : EndCondition()
    
    @Parcelize
    data class AfterCount(val count: Int) : EndCondition()
    
    @Parcelize
    data class OnDate(val date: LocalDate) : EndCondition()
}

// Focus Session
@Parcelize
data class FocusSession(
    val id: String,
    val taskId: String? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val duration: Long = 0, // milliseconds
    val isCompleted: Boolean = false,
    val interruptions: Int = 0,
    val mode: FocusMode = FocusMode.POMODORO,
    val pomodorosCompleted: Int = 0,
    val breakDuration: Long = 0
) : Parcelable

@Parcelize
enum class FocusMode : Parcelable {
    POMODORO,
    STOPWATCH,
    COUNTDOWN,
    HABIT
}

// Habit
@Parcelize
data class Habit(
    val id: String,
    val name: String,
    val icon: String,
    val color: Int,
    val frequency: HabitFrequency,
    val reminders: List<Reminder> = emptyList(),
    val targetDays: Int = 21,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val archived: Boolean = false
) : Parcelable

@Parcelize
data class HabitFrequency(
    val type: FrequencyType,
    val daysOfWeek: List<Int> = emptyList(),
    val timesPerMonth: Int = 1
) : Parcelable

@Parcelize
enum class FrequencyType : Parcelable {
    DAILY,
    WEEKDAYS,
    WEEKENDS,
    SPECIFIC_DAYS,
    TIMES_PER_WEEK,
    TIMES_PER_MONTH
}

@Parcelize
data class HabitCheckIn(
    val id: String,
    val habitId: String,
    val date: LocalDate,
    val timestamp: LocalDateTime,
    val note: String = "",
    val imagePath: String? = null
) : Parcelable

// Folder/Project
@Parcelize
data class Folder(
    val id: String,
    val name: String,
    val icon: String,
    val color: Int,
    val parentId: String? = null,
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
) : Parcelable

// Tag
@Parcelize
data class Tag(
    val id: String,
    val name: String,
    val color: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
) : Parcelable

// Statistics
@Parcelize
data class DailyStats(
    val date: LocalDate,
    val tasksCompleted: Int,
    val tasksCreated: Int,
    val focusTimeMinutes: Int,
    val pomodorosCompleted: Int,
    val habitsChecked: Int
) : Parcelable

// Settings
@Parcelize
data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val primaryColor: Int = 0xFF6200EE.toInt(),
    val useAmoledDark: Boolean = false,
    val language: String = "zh-CN",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val defaultPomodoroDuration: Int = 25,
    val defaultBreakDuration: Int = 5,
    val defaultLongBreakDuration: Int = 15,
    val autoStartBreak: Boolean = false,
    val autoStartNextPomodoro: Boolean = false,
    val strictMode: Boolean = false,
    val screenLockEnabled: Boolean = false,
    val appLockEnabled: Boolean = false,
    val appLockType: AppLockType = AppLockType.PIN,
    val backupEnabled: Boolean = true,
    val autoBackupFrequency: AutoBackupFrequency = AutoBackupFrequency.WEEKLY
) : Parcelable

@Parcelize
enum class ThemeMode : Parcelable {
    LIGHT,
    DARK,
    SYSTEM,
    SCHEDULED
}

@Parcelize
enum class AppLockType : Parcelable {
    NONE,
    PIN,
    PATTERN,
    BIOMETRIC
}

@Parcelize
enum class AutoBackupFrequency : Parcelable {
    DAILY,
    WEEKLY,
    MONTHLY,
    NEVER
}