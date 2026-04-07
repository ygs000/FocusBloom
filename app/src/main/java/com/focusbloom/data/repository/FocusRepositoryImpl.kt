package com.focusbloom.data.repository

import com.focusbloom.data.local.FocusSessionDao
import com.focusbloom.data.local.entity.FocusSessionEntity
import com.focusbloom.domain.model.*
import com.focusbloom.domain.repository.FocusRepository
import com.focusbloom.domain.repository.FocusStats
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.coroutines.CoroutineContext

class FocusRepositoryImpl(
    private val focusSessionDao: FocusSessionDao,
    private val dispatcher: CoroutineContext
) : FocusRepository {

    override fun getAllSessions(): Flow<List<FocusSession>> = focusSessionDao.getAllSessions()
        .map { entities -> entities.map { it.toDomain() } }
        .flowOn(dispatcher)

    override fun getActiveSession(): Flow<FocusSession?> = focusSessionDao.getActiveSession()
        .map { it?.toDomain() }
        .flowOn(dispatcher)

    override fun getSessionsByDate(date: LocalDate): Flow<List<FocusSession>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return focusSessionDao.getSessionsByDate(startOfDay, endOfDay)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatcher)
    }

    override suspend fun startSession(session: FocusSession): Result<String> = withContext(dispatcher) {
        try {
            val entity = session.toEntity()
            val id = focusSessionDao.insertSession(entity)
            Result.success(session.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun endSession(sessionId: String, endTime: java.time.LocalDateTime): Result<Unit> = withContext(dispatcher) {
        try {
            val session = focusSessionDao.getSessionById(sessionId) ?: return@withContext Result.failure(Exception("Session not found"))
            val endTimeMillis = endTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
            val duration = endTimeMillis - session.startTime
            focusSessionDao.endSession(sessionId, endTimeMillis, duration, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> = withContext(dispatcher) {
        try {
            val session = focusSessionDao.getSessionById(sessionId) ?: return@withContext Result.failure(Exception("Session not found"))
            focusSessionDao.deleteSession(session)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Not implemented for minimal version
    override fun getSessionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FocusSession>> = emptyFlow()
    override fun getSessionsByTask(taskId: String): Flow<List<FocusSession>> = emptyFlow()
    override suspend fun getSessionById(sessionId: String): Result<FocusSession?> = Result.success(null)
    override suspend fun updateSession(session: FocusSession): Result<Unit> = Result.success(Unit)
    override suspend fun getTotalFocusTime(startDate: LocalDate, endDate: LocalDate): Result<Long> = Result.success(0)
    override suspend fun getCompletedPomodoros(startDate: LocalDate, endDate: LocalDate): Result<Int> = Result.success(0)
    override suspend fun getFocusStats(startDate: LocalDate, endDate: LocalDate): Result<FocusStats> = Result.success(
        FocusStats(0, 0, 0, 0, 0f, emptyMap(), emptyMap(), emptyMap())
    )
    override suspend fun deleteSessionsByTask(taskId: String): Result<Unit> = Result.success(Unit)
}

private fun FocusSessionEntity.toDomain(): FocusSession {
    return FocusSession(
        id = id,
        taskId = taskId,
        startTime = java.time.LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), java.time.ZoneId.systemDefault()),
        endTime = endTime?.let { java.time.LocalDateTime.ofInstant(Instant.ofEpochMilli(it), java.time.ZoneId.systemDefault()) },
        duration = duration,
        isCompleted = isCompleted,
        interruptions = interruptions,
        mode = FocusMode.valueOf(mode),
        pomodorosCompleted = pomodorosCompleted,
        breakDuration = breakDuration
    )
}

private fun FocusSession.toEntity(): FocusSessionEntity {
    return FocusSessionEntity(
        id = id,
        taskId = taskId,
        startTime = startTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
        endTime = endTime?.toInstant(java.time.ZoneOffset.UTC)?.toEpochMilli(),
        duration = duration,
        isCompleted = isCompleted,
        interruptions = interruptions,
        mode = mode.name,
        pomodorosCompleted = pomodorosCompleted,
        breakDuration = breakDuration
    )
}