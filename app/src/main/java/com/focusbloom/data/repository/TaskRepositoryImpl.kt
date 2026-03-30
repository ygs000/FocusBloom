package com.focusbloom.data.repository

import com.focusbloom.data.local.TaskDao
import com.focusbloom.data.local.entity.TaskEntity
import com.focusbloom.domain.model.*
import com.focusbloom.domain.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.coroutines.CoroutineContext

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val dispatcher: CoroutineContext
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
        .map { entities -> entities.map { it.toDomain() } }
        .flowOn(dispatcher)

    override fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> = 
        taskDao.getTasksByStatus(status.name)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatcher)

    override suspend fun createTask(task: Task): Result<String> = withContext(dispatcher) {
        try {
            val entity = task.toEntity()
            taskDao.insertTask(entity)
            Result.success(task.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> = withContext(dispatcher) {
        try {
            val entity = task.toEntity().copy(updatedAt = Instant.now().epochSecond)
            taskDao.updateTask(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> = withContext(dispatcher) {
        try {
            taskDao.softDeleteTask(taskId, Instant.now().epochSecond)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaskById(taskId: String): Result<Task?> = withContext(dispatcher) {
        try {
            val entity = taskDao.getTaskById(taskId)
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchTasks(query: String): Flow<List<Task>> = 
        taskDao.searchTasks(query)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatcher)

    // Not implemented for minimal version
    override fun getTasksByFolder(folderId: String): Flow<List<Task>> = emptyFlow()
    override fun getTasksByTag(tagId: String): Flow<List<Task>> = emptyFlow()
    override fun getTasksByPriority(priority: Priority): Flow<List<Task>> = emptyFlow()
    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> = emptyFlow()
    override fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>> = emptyFlow()
    override fun getSubTasks(parentTaskId: String): Flow<List<Task>> = emptyFlow()
    override fun getFilteredTasks(filter: TaskFilter): Flow<List<Task>> = emptyFlow()
    override suspend fun batchUpdateStatus(taskIds: List<String>, status: TaskStatus): Result<Unit> = Result.success(Unit)
    override suspend fun batchMoveToFolder(taskIds: List<String>, folderId: String): Result<Unit> = Result.success(Unit)
    override suspend fun batchDelete(taskIds: List<String>): Result<Unit> = Result.success(Unit)
    override suspend fun batchAddTags(taskIds: List<String>, tagIds: List<String>): Result<Unit> = Result.success(Unit)
    override suspend fun moveToRecycleBin(taskId: String): Result<Unit> = Result.success(Unit)
    override suspend fun restoreFromRecycleBin(taskId: String): Result<Unit> = Result.success(Unit)
    override fun getRecycleBinTasks(): Flow<List<Task>> = emptyFlow()
    override suspend fun emptyRecycleBin(): Result<Unit> = Result.success(Unit)
    override suspend fun getTaskStats(startDate: LocalDate, endDate: LocalDate): Result<TaskStats> = Result.success(
        TaskStats(0, 0, 0, 0, 0f, emptyMap(), emptyMap(), emptyMap())
    )
    override fun getCompletionRate(): Flow<Float> = flowOf(0f)
    override fun getTodayTaskCount(): Flow<Int> = flowOf(0)
}

// Extension functions for mapping
private fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        status = TaskStatus.valueOf(status),
        priority = Priority.valueOf(priority),
        dueDate = dueDate?.let { LocalDate.ofEpochDay(it) },
        createdAt = LocalDate.ofInstant(Instant.ofEpochSecond(createdAt), ZoneId.systemDefault()).atStartOfDay(),
        updatedAt = LocalDate.ofInstant(Instant.ofEpochSecond(updatedAt), ZoneId.systemDefault()).atStartOfDay(),
        completedAt = completedAt?.let { LocalDate.ofInstant(Instant.ofEpochSecond(it), ZoneId.systemDefault()).atStartOfDay() },
        estimatedPomodoros = estimatedPomodoros,
        completedPomodoros = completedPomodoros
    )
}

private fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        status = status.name,
        priority = priority.name,
        dueDate = dueDate?.toEpochDay(),
        createdAt = createdAt.toEpochSecond(java.time.ZoneOffset.UTC),
        updatedAt = updatedAt.toEpochSecond(java.time.ZoneOffset.UTC),
        completedAt = completedAt?.toEpochSecond(java.time.ZoneOffset.UTC),
        estimatedPomodoros = estimatedPomodoros,
        completedPomodoros = completedPomodoros
    )
}