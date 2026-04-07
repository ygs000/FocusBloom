package com.focusbloom.data.local

import androidx.room.*
import com.focusbloom.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class,
        FolderEntity::class,
        TagEntity::class,
        TaskTagCrossRef::class,
        FocusSessionEntity::class,
        HabitEntity::class,
        HabitCheckInEntity::class,
        SettingEntity::class,
        DailyStatsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class FocusBloomDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun settingsDao(): SettingsDao
    abstract fun dailyStatsDao(): DailyStatsDao
}