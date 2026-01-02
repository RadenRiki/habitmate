/*
 * OLD ROOM DATABASE FILES - COMMENTED OUT
 * Using Firebase Firestore now instead of Room local database
 *
 * Uncomment this file if you want to use Room again

package com.example.habitmate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habitmate.ui.home.HabitTimeOfDay
import java.time.LocalDate

@Entity(tableName = "habits")
data class HabitEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val title: String,
        val emoji: String,
        val timeOfDay: HabitTimeOfDay,
        val unitLabel: String,
        val current: Int = 0,
        val target: Int = 1,
        val createdDate: Long = LocalDate.now().toEpochDay(),
        val isDoneToday: Boolean = false,
        val streak: Int = 0,
        val selectedDays: List<Boolean> = List(7) { true },
        val weeklyTarget: Int = 0
)
*/
