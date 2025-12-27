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

    suspend fun refreshStreak(habitId: Int) {
        val completedHistory = historyDao.getCompletedHistory(habitId)
        val sortedDates = completedHistory.map { it.date }.sortedDescending().distinct()

        if (sortedDates.isEmpty()) {
            habitDao.updateStreak(habitId, 0)
            return
        }

        val today = java.time.LocalDate.now().toEpochDay()
        val yesterday = today - 1

        // Check if the streak is active (completed today or yesterday)
        // If the last completion was before yesterday, the streak is broken (0),
        // unless we want to display the "last known streak" which usually we don't for active
        // streaks.
        // Standard logic: Streak = 0 if not done today AND not done yesterday.
        // BUT, usually you want to see the streak "pending" for today if you did it yesterday.

        val lastCompletion = sortedDates.first()
        if (lastCompletion < yesterday) {
            habitDao.updateStreak(habitId, 0)
            return
        }

        var currentStreak = 0
        var checkDate = if (lastCompletion == today) today else yesterday
        // If done today, start checking from today. If done yesterday (but not today), start from
        // yesterday.

        for (date in sortedDates) {
            if (date == checkDate) {
                currentStreak++
                checkDate--
            } else if (date < checkDate) {
                // Gap found
                break
            }
            // If date > checkDate (duplicate or future?), ignore
        }

        habitDao.updateStreak(habitId, currentStreak)
    }
}
