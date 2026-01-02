/*
 * OLD ROOM DATABASE FILES - COMMENTED OUT
 * Using Firebase Firestore now instead of Room local database
 *
 * Uncomment this file if you want to use Room again

package com.example.habitmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
        entities = [HabitEntity::class, HabitHistoryEntity::class],
        version = 5,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitHistoryDao(): HabitHistoryDao

    companion object {
        @Volatile private var Instance: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return Instance
                    ?: synchronized(this) {
                        Room.databaseBuilder(context, HabitDatabase::class.java, "habit_database")
                                .fallbackToDestructiveMigration()
                                .build()
                                .also { Instance = it }
                    }
        }
    }
}
*/
