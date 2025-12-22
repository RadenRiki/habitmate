package com.example.habitmate.data.repository

import com.example.habitmate.data.local.HabitDao
import com.example.habitmate.data.local.HabitEntity
import com.example.habitmate.data.local.HabitHistoryDao
import com.example.habitmate.data.local.HabitHistoryEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao, private val historyDao: HabitHistoryDao) {

    // Expose data as Flow to UI
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()

    fun getHistoryForDate(date: Long): Flow<List<HabitHistoryEntity>> =
            historyDao.getHistoryForDate(date)

    suspend fun updateHabitProgress(habitId: Int, date: Long, progress: Int, isDone: Boolean) {
        historyDao.insertOrUpdateHistory(
                HabitHistoryEntity(
                        habitId = habitId,
                        date = date,
                        currentProgress = progress,
                        isDone = isDone
                )
        )
    }

    suspend fun insertHabit(habit: HabitEntity) {
        habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }

    suspend fun resetDailyProgress() {
        // No longer needed to reset habit table, history table handles dates
        habitDao.resetDailyProgress() // Keep for backward compat or cleanup
    }
}
