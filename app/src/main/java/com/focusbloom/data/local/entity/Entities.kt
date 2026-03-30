// Data Layer - Room Database Entities
package com.focusbloom.data.local.entity

import androidx.room.*
import java.time.Instant

/**
 * Task Entity for Room Database
 */
@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["status"]),
        Index(value = ["folder_id"]),
        Index(value = ["due_date"]),
        Index(value = ["created_at"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String = "",
    
    @ColumnInfo(name = "rich_content")
    val richContent: String? = null,
    
    @ColumnInfo(name = "status")
    val status: String = "TODO",
    
    @ColumnInfo(name = "priority")
    val priority: String = "MEDIUM",
    
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    
    @ColumnInfo(name = "due_time")
    val dueTime: String? = null,
    
    @ColumnInfo(name = "folder_id")
    val folderId: String? = null,
    
    @ColumnInfo(name = "parent_task_id")
    val parentTaskId: String? = null,
    
    @ColumnInfo(name = "repeat_rule")
    val repeatRule: String? = null,
    
    @ColumnInfo(name = "estimated_pomodoros")
    val estimatedPomodoros: Int = 0,
    
    @ColumnInfo(name = "completed_pomodoros")
    val completedPomodoros: Int = 0,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    
    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().epochSecond,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = Instant.now().epochSecond,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null
)

/**
 * Folder Entity
 */
@Entity(
    tableName = "folders",
    indices = [
        Index(value = ["parent_id"]),
        Index(value = ["sort_order"])
    ]
)
data class FolderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "icon")
    val icon: String = "folder",
    
    @ColumnInfo(name = "color")
    val color: Int = 0xFF6200EE.toInt(),
    
    @ColumnInfo(name = "parent_id")
    val parentId: String? = null,
    
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().epochSecond
)

/**
 * Tag Entity
 */
@Entity(
    tableName = "tags",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class TagEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "color")
    val color: Int = 0xFF6200EE.toInt(),
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().epochSecond
)

/**
 * Task-Tag Cross Reference for Many-to-Many relationship
 */
@Entity(
    tableName = "task_tags",
    primaryKeys = ["task_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tag_id"])
    ]
)
data class TaskTagCrossRef(
    @ColumnInfo(name = "task_id")
    val taskId: String,
    
    @ColumnInfo(name = "tag_id")
    val tagId: String
)

/**
 * Focus Session Entity
 */
@Entity(
    tableName = "focus_sessions",
    indices = [
        Index(value = ["task_id"]),
        Index(value = ["start_time"]),
        Index(value = ["is_completed"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class FocusSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "task_id")
    val taskId: String? = null,
    
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    
    @ColumnInfo(name = "end_time")
    val endTime: Long? = null,
    
    @ColumnInfo(name = "duration")
    val duration: Long = 0, // milliseconds
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "interruptions")
    val interruptions: Int = 0,
    
    @ColumnInfo(name = "mode")
    val mode: String = "POMODORO",
    
    @ColumnInfo(name = "pomodoros_completed")
    val pomodorosCompleted: Int = 0,
    
    @ColumnInfo(name = "break_duration")
    val breakDuration: Long = 0
)

/**
 * Habit Entity
 */
@Entity(
    tableName = "habits",
    indices = [
        Index(value = ["archived"]),
        Index(value = ["created_at"])
    ]
)
data class HabitEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "icon")
    val icon: String = "star",
    
    @ColumnInfo(name = "color")
    val color: Int = 0xFF6200EE.toInt(),
    
    @ColumnInfo(name = "frequency_type")
    val frequencyType: String = "DAILY",
    
    @ColumnInfo(name = "frequency_days")
    val frequencyDays: String = "[]", // JSON array of day numbers
    
    @ColumnInfo(name = "frequency_times")
    val frequencyTimes: Int = 1,
    
    @ColumnInfo(name = "target_days")
    val targetDays: Int = 21,
    
    @ColumnInfo(name = "reminders")
    val reminders: String = "[]", // JSON array of reminders
    
    @ColumnInfo(name = "archived")
    val archived: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().epochSecond
)

/**
 * Habit Check-in Entity
 */
@Entity(
    tableName = "habit_checkins",
    indices = [
        Index(value = ["habit_id"]),
        Index(value = ["date"]),
        Index(value = ["habit_id", "date"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitCheckInEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "habit_id")
    val habitId: String,
    
    @ColumnInfo(name = "date")
    val date: Long, // epoch day
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    
    @ColumnInfo(name = "note")
    val note: String = "",
    
    @ColumnInfo(name = "image_path")
    val imagePath: String? = null
)

/**
 * App Settings Entity (Key-Value store)
 */
@Entity(
    tableName = "app_settings",
    indices = [
        Index(value = ["key"], unique = true)
    ]
)
data class SettingEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,
    
    @ColumnInfo(name = "value")
    val value: String,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = Instant.now().epochSecond
)

/**
 * Daily Statistics Entity
 */
@Entity(
    tableName = "daily_stats",
    indices = [
        Index(value = ["date"], unique = true)
    ]
)
data class DailyStatsEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: Long, // epoch day
    
    @ColumnInfo(name = "tasks_completed")
    val tasksCompleted: Int = 0,
    
    @ColumnInfo(name = "tasks_created")
    val tasksCreated: Int = 0,
    
    @ColumnInfo(name = "focus_time_minutes")
    val focusTimeMinutes: Int = 0,
    
    @ColumnInfo(name = "pomodoros_completed")
    val pomodorosCompleted: Int = 0,
    
    @ColumnInfo(name = "habits_checked")
    val habitsChecked: Int = 0,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = Instant.now().epochSecond
)