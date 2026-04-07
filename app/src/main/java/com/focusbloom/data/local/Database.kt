package com.focusbloom.data.local

import androidx.room.*
import com.focusbloom.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE is_deleted = 0 ORDER BY created_at DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId AND is_deleted = 0")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE status = :status AND is_deleted = 0")
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET is_deleted = 1, deleted_at = :timestamp WHERE id = :taskId")
    suspend fun softDeleteTask(taskId: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' AND is_deleted = 0")
    fun searchTasks(query: String): Flow<List<TaskEntity>>
}

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY start_time DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE end_time IS NULL ORDER BY start_time DESC LIMIT 1")
    fun getActiveSession(): Flow<FocusSessionEntity?>

    @Query("SELECT * FROM focus_sessions WHERE start_time >= :startTime AND start_time <= :endTime")
    fun getSessionsByDate(startTime: Long, endTime: Long): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): FocusSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("UPDATE focus_sessions SET end_time = :endTime, duration = :duration, is_completed = :isCompleted WHERE id = :sessionId")
    suspend fun endSession(sessionId: String, endTime: Long, duration: Long, isCompleted: Boolean)

    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)

    @Query("SELECT SUM(duration) FROM focus_sessions WHERE start_time >= :startTime AND start_time <= :endTime")
    suspend fun getTotalFocusTime(startTime: Long, endTime: Long): Long?

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE is_completed = 1 AND start_time >= :startTime AND start_time <= :endTime")
    suspend fun getCompletedSessionsCount(startTime: Long, endTime: Long): Int
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    suspend fun getSetting(key: String): SettingEntity?

    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    fun getSettingFlow(key: String): Flow<SettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)

    @Query("DELETE FROM app_settings WHERE `key` = :key")
    suspend fun deleteSetting(key: String)
}

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllStats(): Flow<List<DailyStatsEntity>>

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getStatsByDate(date: Long): DailyStatsEntity?

    @Query("SELECT * FROM daily_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getStatsByDateRange(startDate: Long, endDate: Long): Flow<List<DailyStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: DailyStatsEntity)

    @Query("UPDATE daily_stats SET tasks_completed = tasks_completed + 1 WHERE date = :date")
    suspend fun incrementTasksCompleted(date: Long)

    @Query("UPDATE daily_stats SET focus_time_minutes = focus_time_minutes + :minutes WHERE date = :date")
    suspend fun addFocusTime(date: Long, minutes: Int)

    @Query("UPDATE daily_stats SET pomodoros_completed = pomodoros_completed + 1 WHERE date = :date")
    suspend fun incrementPomodoros(date: Long)
}