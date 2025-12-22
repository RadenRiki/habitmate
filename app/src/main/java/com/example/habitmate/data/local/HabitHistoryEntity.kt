package com.example.habitmate.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "habit_history",
        foreignKeys =
                [
                        ForeignKey(
                                entity = HabitEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["habitId"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices = [Index(value = ["habitId", "date"], unique = true)]
)
data class HabitHistoryEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val habitId: Int,
        val date: Long, // Epoch Day
        val currentProgress: Int,
        val isDone: Boolean
)
