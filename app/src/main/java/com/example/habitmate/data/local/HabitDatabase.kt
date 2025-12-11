package com.example.habitmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HabitEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile private var Instance: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return Instance
                    ?: synchronized(this) {
                        Room.databaseBuilder(context, HabitDatabase::class.java, "habit_database")
                                .fallbackToDestructiveMigration() // Wipes data on schema change
                                // (good for dev)
                                .build()
                                .also { Instance = it }
                    }
        }
    }
}
