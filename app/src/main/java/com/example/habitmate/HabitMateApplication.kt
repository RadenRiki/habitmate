package com.example.habitmate

import android.app.Application
import com.example.habitmate.data.local.HabitDatabase
import com.example.habitmate.data.repository.HabitRepository

class HabitMateApplication : Application() {

    val database by lazy { HabitDatabase.getDatabase(this) }
    val repository by lazy { HabitRepository(database.habitDao(), database.habitHistoryDao()) }
}
