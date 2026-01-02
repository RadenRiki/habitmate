/*
 * OLD ROOM DATABASE FILES - COMMENTED OUT
 * Using Firebase Firestore now instead of Room local database
 *
 * Uncomment this file if you want to use Room again

package com.example.habitmate.data.local

import androidx.room.TypeConverter
import com.example.habitmate.ui.home.HabitTimeOfDay

class Converters {
    @TypeConverter
    fun fromHabitTimeOfDay(value: HabitTimeOfDay): String {
        return value.name
    }

    @TypeConverter
    fun toHabitTimeOfDay(value: String): HabitTimeOfDay {
        return try {
            HabitTimeOfDay.valueOf(value)
        } catch (e: Exception) {
            HabitTimeOfDay.ANYTIME // Fallback
        }
    }

    @TypeConverter
    fun fromBooleanList(list: List<Boolean>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toBooleanList(data: String): List<Boolean> {
        if (data.isEmpty()) return emptyList()
        return data.split(",").map { it.toBoolean() }
    }
}
*/
