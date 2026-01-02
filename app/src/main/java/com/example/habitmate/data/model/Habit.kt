package com.example.habitmate.data.model

import com.example.habitmate.ui.home.HabitTimeOfDay
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/** Firestore data model for Habit This replaces HabitEntity (Room) for cloud storage */
data class Habit(
        @DocumentId val id: String = "",
        val title: String = "",
        val emoji: String = "",
        val timeOfDay: String = HabitTimeOfDay.ANYTIME.name,
        val unitLabel: String = "",
        val current: Int = 0,
        val target: Int = 1,
        val createdDate: Long = java.time.LocalDate.now().toEpochDay(),

        // Use @PropertyName to match Firestore field name
        @get:PropertyName("isDoneToday")
        @set:PropertyName("isDoneToday")
        var isDoneToday: Boolean = false,
        val streak: Int = 0,
        val selectedDays: List<Boolean> = List(7) { true },
        val weeklyTarget: Int = 0
) {
    // No-arg constructor required for Firestore deserialization
    constructor() : this(id = "")

    fun toTimeOfDay(): HabitTimeOfDay {
        return try {
            HabitTimeOfDay.valueOf(timeOfDay)
        } catch (e: Exception) {
            HabitTimeOfDay.ANYTIME
        }
    }
}
