/*
 * OLD ROOM DATABASE FILES - COMMENTED OUT
 * Using Firebase Firestore now instead of Room local database
 *
 * Uncomment this file if you want to use Room again

package com.example.habitmate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitHistoryDao {
    @Query("SELECT * FROM habit_history") fun getAllHistory(): Flow<List<HabitHistoryEntity>>

    @Query("SELECT * FROM habit_history WHERE date = :date")
    fun getHistoryForDate(date: Long): Flow<List<HabitHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHistory(history: HabitHistoryEntity)

    @Query("SELECT * FROM habit_history WHERE habitId = :habitId")
    fun getHistoryForHabit(habitId: Int): Flow<List<HabitHistoryEntity>>

    @Query("SELECT * FROM habit_history WHERE habitId = :habitId AND isDone = 1")
    suspend fun getCompletedHistory(habitId: Int): List<HabitHistoryEntity>
}
*/
