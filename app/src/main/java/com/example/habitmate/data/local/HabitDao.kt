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

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertHabit(habit: HabitEntity)

    @Update suspend fun updateHabit(habit: HabitEntity)

    @Delete suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET current = 0, isDoneToday = 0") suspend fun resetDailyProgress()
}
