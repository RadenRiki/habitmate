package com.example.habitmate

import android.app.Application
import android.util.Log
import com.example.habitmate.data.repository.FirestoreRepository
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

// OLD ROOM IMPORTS - COMMENTED OUT
// import com.example.habitmate.data.local.HabitDatabase
// import com.example.habitmate.data.repository.HabitRepository

class HabitMateApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // OLD ROOM DATABASE - COMMENTED OUT
    // val database by lazy { HabitDatabase.getDatabase(this) }
    // val repository by lazy { HabitRepository(database.habitDao(), database.habitHistoryDao()) }

    // NEW FIRESTORE REPOSITORY
    val repository by lazy { FirestoreRepository() }

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Anonymous Auth
        applicationScope.launch {
            val success = repository.ensureAuthenticated()
            Log.d("HabitMateApp", "Anonymous auth initialized: $success")
        }
    }
}
