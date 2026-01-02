package com.example.habitmate.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

/**
 * Firestore data model for Habit History This replaces HabitHistoryEntity (Room) for cloud storage
 */
data class HabitHistory(
        @DocumentId val id: String = "",
        val habitId: String = "",
        val date: Long = 0, // Epoch Day
        val currentProgress: Int = 0,

        // Use @PropertyName to match Firestore field name
        // Firestore stores this as "isDone" but Kotlin Boolean with "is" prefix has special
        // handling
        @get:PropertyName("isDone") @set:PropertyName("isDone") var isDone: Boolean = false
) {
    // No-arg constructor required for Firestore deserialization
    constructor() : this(id = "")
}
