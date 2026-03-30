package com.focusbloom.data.repository

import com.focusbloom.data.local.SettingsDao
import com.focusbloom.data.local.entity.SettingEntity
import com.focusbloom.domain.model.*
import com.focusbloom.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class SettingsRepositoryImpl(
    private val settingsDao: SettingsDao,
    private val dispatcher: CoroutineContext
) : SettingsRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getSettings(): Result<AppSettings> = withContext(dispatcher) {
        try {
            val settings = loadSettingsFromDb()
            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSettingsFlow(): Flow<AppSettings> = flow {
        emit(loadSettingsFromDb())
    }.flowOn(dispatcher)

    private suspend fun loadSettingsFromDb(): AppSettings {
        return AppSettings(
            theme = getSetting("theme")?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM,
            primaryColor = getSetting("primary_color")?.toIntOrNull() ?: 0xFF6200EE.toInt(),
            useAmoledDark = getSetting("use_amoled_dark")?.toBoolean() ?: false,
            language = getSetting("language") ?: "zh-CN",
            soundEnabled = getSetting("sound_enabled")?.toBoolean() ?: true,
            vibrationEnabled = getSetting("vibration_enabled")?.toBoolean() ?: true,
            defaultPomodoroDuration = getSetting("default_pomodoro_duration")?.toIntOrNull() ?: 25,
            defaultBreakDuration = getSetting("default_break_duration")?.toIntOrNull() ?: 5,
            defaultLongBreakDuration = getSetting("default_long_break_duration")?.toIntOrNull() ?: 15,
            autoStartBreak = getSetting("auto_start_break")?.toBoolean() ?: false,
            autoStartNextPomodoro = getSetting("auto_start_next_pomodoro")?.toBoolean() ?: false,
            strictMode = getSetting("strict_mode")?.toBoolean() ?: false,
            screenLockEnabled = getSetting("screen_lock_enabled")?.toBoolean() ?: false,
            appLockEnabled = getSetting("app_lock_enabled")?.toBoolean() ?: false,
            appLockType = getSetting("app_lock_type")?.let { AppLockType.valueOf(it) } ?: AppLockType.PIN,
            backupEnabled = getSetting("backup_enabled")?.toBoolean() ?: true,
            autoBackupFrequency = getSetting("auto_backup_frequency")?.let { AutoBackupFrequency.valueOf(it) } ?: AutoBackupFrequency.WEEKLY
        )
    }

    private suspend fun getSetting(key: String): String? {
        return settingsDao.getSetting(key)?.value
    }

    private suspend fun setSetting(key: String, value: String) {
        settingsDao.setSetting(
            SettingEntity(
                key = key,
                value = value,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun updateSettings(settings: AppSettings): Result<Unit> = withContext(dispatcher) {
        try {
            setSetting("theme", settings.theme.name)
            setSetting("primary_color", settings.primaryColor.toString())
            setSetting("use_amoled_dark", settings.useAmoledDark.toString())
            setSetting("language", settings.language)
            setSetting("sound_enabled", settings.soundEnabled.toString())
            setSetting("vibration_enabled", settings.vibrationEnabled.toString())
            setSetting("default_pomodoro_duration", settings.defaultPomodoroDuration.toString())
            setSetting("default_break_duration", settings.defaultBreakDuration.toString())
            setSetting("default_long_break_duration", settings.defaultLongBreakDuration.toString())
            setSetting("auto_start_break", settings.autoStartBreak.toString())
            setSetting("auto_start_next_pomodoro", settings.autoStartNextPomodoro.toString())
            setSetting("strict_mode", settings.strictMode.toString())
            setSetting("screen_lock_enabled", settings.screenLockEnabled.toString())
            setSetting("app_lock_enabled", settings.appLockEnabled.toString())
            setSetting("app_lock_type", settings.appLockType.name)
            setSetting("backup_enabled", settings.backupEnabled.toString())
            setSetting("auto_backup_frequency", settings.autoBackupFrequency.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Convenience methods for individual setting updates
    override suspend fun updateTheme(theme: ThemeMode): Result<Unit> = withContext(dispatcher) {
        setSetting("theme", theme.name)
        Result.success(Unit)
    }

    override suspend fun updatePrimaryColor(color: Int): Result<Unit> = withContext(dispatcher) {
        setSetting("primary_color", color.toString())
        Result.success(Unit)
    }

    override suspend fun toggleAmoledDark(useAmoled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("use_amoled_dark", useAmoled.toString())
        Result.success(Unit)
    }

    override suspend fun updateLanguage(language: String): Result<Unit> = withContext(dispatcher) {
        setSetting("language", language)
        Result.success(Unit)
    }

    override suspend fun toggleSound(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("sound_enabled", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun toggleVibration(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("vibration_enabled", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun updatePomodoroDuration(minutes: Int): Result<Unit> = withContext(dispatcher) {
        setSetting("default_pomodoro_duration", minutes.toString())
        Result.success(Unit)
    }

    override suspend fun updateBreakDuration(minutes: Int): Result<Unit> = withContext(dispatcher) {
        setSetting("default_break_duration", minutes.toString())
        Result.success(Unit)
    }

    override suspend fun updateLongBreakDuration(minutes: Int): Result<Unit> = withContext(dispatcher) {
        setSetting("default_long_break_duration", minutes.toString())
        Result.success(Unit)
    }

    override suspend fun toggleAutoStartBreak(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("auto_start_break", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun toggleAutoStartNextPomodoro(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("auto_start_next_pomodoro", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun toggleStrictMode(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("strict_mode", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun toggleScreenLock(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("screen_lock_enabled", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun toggleAppLock(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("app_lock_enabled", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun updateAppLockType(type: AppLockType): Result<Unit> = withContext(dispatcher) {
        setSetting("app_lock_type", type.name)
        Result.success(Unit)
    }

    override suspend fun toggleBackup(enabled: Boolean): Result<Unit> = withContext(dispatcher) {
        setSetting("backup_enabled", enabled.toString())
        Result.success(Unit)
    }

    override suspend fun updateAutoBackupFrequency(frequency: AutoBackupFrequency): Result<Unit> = withContext(dispatcher) {
        setSetting("auto_backup_frequency", frequency.name)
        Result.success(Unit)
    }

    override suspend fun resetToDefaults(): Result<Unit> = withContext(dispatcher) {
        val defaults = AppSettings()
        updateSettings(defaults)
        Result.success(Unit)
    }
}