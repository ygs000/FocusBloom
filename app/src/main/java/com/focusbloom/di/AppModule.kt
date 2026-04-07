// Dependency Injection Module
package com.focusbloom.di

import android.content.Context
import androidx.room.Room
import com.focusbloom.data.local.*
import com.focusbloom.data.repository.*
import com.focusbloom.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusBloomDatabase {
        return Room.databaseBuilder(
            context,
            FocusBloomDatabase::class.java,
            "focusbloom.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: FocusBloomDatabase) = database.taskDao()

    @Provides
    @Singleton
    fun provideFocusSessionDao(database: FocusBloomDatabase) = database.focusSessionDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: FocusBloomDatabase) = database.settingsDao()

    @Provides
    @Singleton
    fun provideDailyStatsDao(database: FocusBloomDatabase) = database.dailyStatsDao()

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineContext = Dispatchers.IO

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        @IoDispatcher dispatcher: CoroutineContext
    ): TaskRepository {
        return TaskRepositoryImpl(taskDao, dispatcher)
    }

    @Provides
    @Singleton
    fun provideFocusRepository(
        focusSessionDao: FocusSessionDao,
        @IoDispatcher dispatcher: CoroutineContext
    ): FocusRepository {
        return FocusRepositoryImpl(focusSessionDao, dispatcher)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        settingsDao: SettingsDao,
        @IoDispatcher dispatcher: CoroutineContext
    ): SettingsRepository {
        return SettingsRepositoryImpl(settingsDao, dispatcher)
    }
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher