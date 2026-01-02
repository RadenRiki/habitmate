/*
 * OLD ROOM DATABASE FILES - COMMENTED OUT
 * Using Firebase Firestore now instead of Room local database
 *
 * Uncomment this file if you want to use Room again

package com.example.habitmate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdDate DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id") suspend fun getHabitById(id: Int): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertHabit(habit: HabitEntity)

    @Update suspend fun updateHabit(habit: HabitEntity)

    @Delete suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET current = 0, isDoneToday = 0") suspend fun resetDailyProgress()

    @Query("UPDATE habits SET streak = :streak WHERE id = :id")
    suspend fun updateStreak(id: Int, streak: Int)
}
*/
