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

    val allHistory: Flow<List<HabitHistoryEntity>> = historyDao.getAllHistory()

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

        val habit = habitDao.getHabitById(habitId) ?: return // Should not happen
        val selectedDays = habit.selectedDays // List<Boolean> index 0=Sun, 6=Sat

        val today = java.time.LocalDate.now().toEpochDay()

        // Find the last valid completion date (should be today or the most recent scheduled day
        // before today)
        // Actually, we just look at history.
        val lastCompletion = sortedDates.first()

        // Check if the gap between today and last completion contains any "Missed Opportunities"
        // If today is a Scheduled Day, and we haven't done it, and last completion was before
        // today:
        // Then streak depends on if we missed it today.
        // Usually, an active streak includes today if done, or yesterday if done.

        // Revised Logic:
        // Iterate backwards DAY BY DAY from the "Last Streak End Date" (which is either today or
        // last completion).
        // But simpler: Start from the most recent completion.
        // Go backwards. If there is a gap, check if the days in the gap were scheduled.
        // If a scheduled day is found in the gap, and it was NOT done, streak breaks.
        // Also check "gap forward" from last completion to Today to ensure we haven't missed a
        // scheduled day in between.

        // 1. Check if the streak is still "Active"
        // Active means: We haven't missed any scheduled deadline up to Today.
        // Iterate form Today backwards to lastCompletion.
        // If we find a Scheduled Day that is NOT the lastCompletion, then streak is broken (it's
        // 0).
        // Exception: If current progress is 0 but the day isn't over... apps usually show streak
        // from yesterday.
        // But detailed logic:

        // Let's count backwards from lastCompletion.
        var currentStreak = 0
        var checkDate = lastCompletion

        // Check forward from lastCompletion + 1 to Today.
        // If there is any scheduled day in (lastCompletion+1 .. today-1), then streak is broken
        // (0).
        // If Today is scheduled and NOT done, streak is pending (show last streak? or 0?).
        // Usually: Displayed Streak = number of consecutive executions.
        // If I missed yesterday (scheduled), my streak is 0.

        var isStreakBroken = false
        // Check gap between last completion and Today
        for (d in (lastCompletion + 1) until today) {
            val dateObj = java.time.LocalDate.ofEpochDay(d)
            val dayOfWeek = dateObj.dayOfWeek.value % 7 // 0=Sun
            if (selectedDays[dayOfWeek]) {
                isStreakBroken = true // Missed a scheduled day!
                break
            }
        }

        // Also: If Today is scheduled, and not done (which is true if lastCompletion < today),
        // the streak is NOT broken yet (it's pending).
        // But if yesterday was scheduled and missed, it IS broken.
        // The loop above checks strictly between lastCompletion and Today.
        // (lastCompletion + 1 until today) covers yesterday if lastCompletion was
        // day-before-yesterday.

        if (isStreakBroken) {
            habitDao.updateStreak(habitId, 0)
            return
        }

        // 2. Calculate the streak length by going backwards from lastCompletion
        // We count each COMPLETED date.
        // We verify that between completed dates, there were NO missed scheduled days.

        currentStreak = 1 // Count the lastCompletion itself
        var previousDate = lastCompletion

        for (i in 1 until sortedDates.size) { // start from second latest
            val currentDate = sortedDates[i]

            // Check gap between currentDate and previousDate
            var gapBroken = false
            for (d in (currentDate + 1) until previousDate) {
                val dateObj = java.time.LocalDate.ofEpochDay(d)
                val dayOfWeek = dateObj.dayOfWeek.value % 7
                if (selectedDays[dayOfWeek]) {
                    gapBroken = true
                    break
                }
            }

            if (gapBroken) {
                break // Streak ends here
            } else {
                currentStreak++
                previousDate = currentDate
            }
        }

        habitDao.updateStreak(habitId, currentStreak)
    }
}
