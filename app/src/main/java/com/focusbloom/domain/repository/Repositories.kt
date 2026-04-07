// Domain Layer - Repository Interfaces
package com.focusbloom.domain.repository

import com.focusbloom.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

/**
 * Task repository interface - defines all task-related operations
 */
interface TaskRepository {
    // CRUD operations
    suspend fun createTask(task: Task): Result<String>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun getTaskById(taskId: String): Result<Task?>
    
    // Query operations with Flow for reactive UI
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>
    fun getTasksByFolder(folderId: String): Flow<List<Task>>
    fun getTasksByTag(tagId: String): Flow<List<Task>>
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>
    fun getSubTasks(parentTaskId: String): Flow<List<Task>>
    
    // Search and filter
    fun searchTasks(query: String): Flow<List<Task>>
    fun getFilteredTasks(filter: TaskFilter): Flow<List<Task>>
    
    // Batch operations
    suspend fun batchUpdateStatus(taskIds: List<String>, status: TaskStatus): Result<Unit>
    suspend fun batchMoveToFolder(taskIds: List<String>, folderId: String): Result<Unit>
    suspend fun batchDelete(taskIds: List<String>): Result<Unit>
    suspend fun batchAddTags(taskIds: List<String>, tagIds: List<String>): Result<Unit>
    
    // Recycle bin
    suspend fun moveToRecycleBin(taskId: String): Result<Unit>
    suspend fun restoreFromRecycleBin(taskId: String): Result<Unit>
    fun getRecycleBinTasks(): Flow<List<Task>>
    suspend fun emptyRecycleBin(): Result<Unit>
    
    // Statistics
    suspend fun getTaskStats(startDate: LocalDate, endDate: LocalDate): Result<TaskStats>
    fun getCompletionRate(): Flow<Float>
    fun getTodayTaskCount(): Flow<Int>
}

/**
 * Task filter data class for complex filtering
 */
data class TaskFilter(
    val statuses: List<TaskStatus> = emptyList(),
    val priorities: List<Priority> = emptyList(),
    val folderIds: List<String> = emptyList(),
    val tagIds: List<String> = emptyList(),
    val dateRange: Pair<LocalDate, LocalDate>? = null,
    val hasDueDate: Boolean? = null,
    val hasReminder: Boolean? = null,
    val isRecurring: Boolean? = null,
    val searchQuery: String = "",
    val sortBy: SortOption = SortOption.DUE_DATE,
    val sortAscending: Boolean = true
)

enum class SortOption {
    DUE_DATE,
    PRIORITY,
    CREATED_DATE,
    COMPLETED_DATE,
    TITLE,
    CUSTOM
}

data class TaskStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val overdueTasks: Int,
    val completionRate: Float,
    val tasksByPriority: Map<Priority, Int>,
    val tasksByStatus: Map<TaskStatus, Int>,
    val dailyCompletions: Map<LocalDate, Int>
)

/**
 * Folder repository interface
 */
interface FolderRepository {
    suspend fun createFolder(folder: Folder): Result<String>
    suspend fun updateFolder(folder: Folder): Result<Unit>
    suspend fun deleteFolder(folderId: String): Result<Unit>
    fun getFolderById(folderId: String): Flow<Folder?>
    fun getAllFolders(): Flow<List<Folder>>
    fun getRootFolders(): Flow<List<Folder>>
    fun getSubFolders(parentId: String): Flow<List<Folder>>
    suspend fun reorderFolders(orderedIds: List<String>): Result<Unit>
}

/**
 * Tag repository interface
 */
interface TagRepository {
    suspend fun createTag(tag: Tag): Result<String>
    suspend fun updateTag(tag: Tag): Result<Unit>
    suspend fun deleteTag(tagId: String): Result<Unit>
    fun getTagById(tagId: String): Flow<Tag?>
    fun getAllTags(): Flow<List<Tag>>
    fun getTagsByIds(tagIds: List<String>): Flow<List<Tag>>
    fun searchTags(query: String): Flow<List<Tag>>
}

/**
 * Focus session repository interface
 */
interface FocusRepository {
    suspend fun startSession(session: FocusSession): Result<String>
    suspend fun updateSession(session: FocusSession): Result<Unit>
    suspend fun endSession(sessionId: String, endTime: LocalDateTime): Result<Unit>
    suspend fun getSessionById(sessionId: String): Result<FocusSession?>
    
    fun getAllSessions(): Flow<List<FocusSession>>
    fun getSessionsByDate(date: LocalDate): Flow<List<FocusSession>>
    fun getSessionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FocusSession>>
    fun getSessionsByTask(taskId: String): Flow<List<FocusSession>>
    fun getActiveSession(): Flow<FocusSession?>
    
    suspend fun getTotalFocusTime(startDate: LocalDate, endDate: LocalDate): Result<Long>
    suspend fun getCompletedPomodoros(startDate: LocalDate, endDate: LocalDate): Result<Int>
    suspend fun getFocusStats(startDate: LocalDate, endDate: LocalDate): Result<FocusStats>
    
    suspend fun deleteSession(sessionId: String): Result<Unit>
    suspend fun deleteSessionsByTask(taskId: String): Result<Unit>
}

data class FocusStats(
    val totalSessions: Int,
    val totalFocusTime: Long, // milliseconds
    val averageSessionDuration: Long,
    val totalPomodoros: Int,
    val completionRate: Float,
    val dailyFocusTime: Map<LocalDate, Long>,
    val hourlyDistribution: Map<Int, Int>,
    val taskFocusDistribution: Map<String, Long>
)

/**
 * Habit repository interface
 */
interface HabitRepository {
    suspend fun createHabit(habit: Habit): Result<String>
    suspend fun updateHabit(habit: Habit): Result<Unit>
    suspend fun deleteHabit(habitId: String): Result<Unit>
    suspend fun archiveHabit(habitId: String, archived: Boolean): Result<Unit>
    fun getHabitById(habitId: String): Flow<Habit?>
    fun getAllHabits(): Flow<List<Habit>>
    fun getActiveHabits(): Flow<List<Habit>>
    fun getArchivedHabits(): Flow<List<Habit>>
    
    suspend fun checkIn(habitId: String, date: LocalDate, note: String, imagePath: String?): Result<String>
    suspend fun removeCheckIn(habitId: String, date: LocalDate): Result<Unit>
    suspend fun updateCheckIn(checkIn: HabitCheckIn): Result<Unit>
    fun getCheckInsByHabit(habitId: String): Flow<List<HabitCheckIn>>
    fun getCheckInsByDate(date: LocalDate): Flow<List<HabitCheckIn>>
    fun getCheckInsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<HabitCheckIn>>
    fun isHabitCheckedOnDate(habitId: String, date: LocalDate): Flow<Boolean>
    
    suspend fun getHabitStats(habitId: String): Result<HabitStats>
    suspend fun getAllHabitsStats(): Result<Map<String, HabitStats>>
    fun getTodayHabits(): Flow<List<Habit>>
    fun getHabitsForDate(date: LocalDate): Flow<List<Habit>>
}

data class HabitStats(
    val totalCheckIns: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val completionRate: Float,
    val monthlyStats: Map<YearMonth, MonthStats>,
    val weeklyStats: Map<Int, WeekStats>,
    val checkInCalendar: Map<LocalDate, Boolean>
)

data class MonthStats(
    val totalDays: Int,
    val checkInDays: Int,
    val completionRate: Float
)

data class WeekStats(
    val totalDays: Int,
    val checkInDays: Int,
    val completionRate: Float
)

/**
 * Settings repository interface
 */
interface SettingsRepository {
    suspend fun getSettings(): Result<AppSettings>
    fun getSettingsFlow(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings): Result<Unit>
    suspend fun updateTheme(theme: ThemeMode): Result<Unit>
    suspend fun updatePrimaryColor(color: Int): Result<Unit>
    suspend fun toggleAmoledDark(useAmoled: Boolean): Result<Unit>
    suspend fun updateLanguage(language: String): Result<Unit>
    suspend fun toggleSound(enabled: Boolean): Result<Unit>
    suspend fun toggleVibration(enabled: Boolean): Result<Unit>
    suspend fun updatePomodoroDuration(minutes: Int): Result<Unit>
    suspend fun updateBreakDuration(minutes: Int): Result<Unit>
    suspend fun updateLongBreakDuration(minutes: Int): Result<Unit>
    suspend fun toggleAutoStartBreak(enabled: Boolean): Result<Unit>
    suspend fun toggleAutoStartNextPomodoro(enabled: Boolean): Result<Unit>
    suspend fun toggleStrictMode(enabled: Boolean): Result<Unit>
    suspend fun toggleScreenLock(enabled: Boolean): Result<Unit>
    suspend fun toggleAppLock(enabled: Boolean): Result<Unit>
    suspend fun updateAppLockType(type: AppLockType): Result<Unit>
    suspend fun toggleBackup(enabled: Boolean): Result<Unit>
    suspend fun updateAutoBackupFrequency(frequency: AutoBackupFrequency): Result<Unit>
    suspend fun resetToDefaults(): Result<Unit>
}

/**
 * Backup/Restore repository interface
 */
interface BackupRepository {
    suspend fun createBackup(): Result<String>
    suspend fun restoreFromBackup(backupPath: String): Result<Unit>
    suspend fun exportToCsv(type: ExportType, startDate: LocalDate?, endDate: LocalDate?): Result<String>
    suspend fun importFromCsv(filePath: String, type: ImportType): Result<Unit>
    suspend fun migrateToNewDevice(exportPath: String): Result<String>
    suspend fun importFromMigration(migrationPath: String): Result<Unit>
    fun getBackupList(): Flow<List<BackupInfo>>
    suspend fun deleteBackup(backupPath: String): Result<Unit>
    suspend fun autoBackup(): Result<Unit>
}

data class BackupInfo(
    val path: String,
    val createdAt: LocalDateTime,
    val size: Long,
    val version: Int
)

enum class ExportType {
    TASKS,
    FOCUS_SESSIONS,
    HABITS,
    ALL
}

enum class ImportType {
    REPLACE,
    MERGE,
    SMART_MERGE
}