package com.example.habitmate.data.repository

import android.util.Log
import com.example.habitmate.data.model.Habit
import com.example.habitmate.data.model.HabitHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Repository for Firebase Firestore operations Uses Anonymous Authentication for user isolation */
class FirestoreRepository {

    companion object {
        private const val TAG = "FirestoreRepository"
    }

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Get current user ID from Firebase Auth Uses anonymous auth - each device gets a unique user
     * ID
     */
    private val userId: String
        get() = auth.currentUser?.uid ?: "default_user"

    private val habitsCollection
        get() = db.collection("users").document(userId).collection("habits")

    private val historyCollection
        get() = db.collection("users").document(userId).collection("history")

    /** Sign in anonymously if not already signed in */
    suspend fun ensureAuthenticated(): Boolean {
        return try {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
                Log.d(TAG, "Signed in anonymously with uid: ${auth.currentUser?.uid}")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous auth failed", e)
            false
        }
    }

    // ==================== HABITS ====================

    /** Get all habits as Flow (real-time updates) */
    val allHabits: Flow<List<Habit>> = callbackFlow {
        val listenerRegistration: ListenerRegistration =
                habitsCollection.orderBy("createdDate", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.e(TAG, "Error fetching habits", error)
                                trySend(emptyList())
                                return@addSnapshotListener
                            }

                            val habits =
                                    snapshot?.documents?.mapNotNull { doc ->
                                        try {
                                            doc.toObject(Habit::class.java)
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Error deserializing habit", e)
                                            null
                                        }
                                    }
                                            ?: emptyList()

                            Log.d(TAG, "Fetched ${habits.size} habits")
                            trySend(habits)
                        }

        awaitClose { listenerRegistration.remove() }
    }

    /** Get a single habit by ID */
    suspend fun getHabitById(id: String): Habit? {
        return try {
            val doc = habitsCollection.document(id).get().await()
            doc.toObject(Habit::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting habit by id", e)
            null
        }
    }

    /** Insert a new habit */
    suspend fun insertHabit(habit: Habit): String {
        val docRef = habitsCollection.document()
        val habitWithId = habit.copy(id = docRef.id)
        docRef.set(habitWithId).await()
        Log.d(TAG, "Inserted habit with id: ${docRef.id}")
        return docRef.id
    }

    /** Update an existing habit */
    suspend fun updateHabit(habit: Habit) {
        habitsCollection.document(habit.id).set(habit).await()
        Log.d(TAG, "Updated habit: ${habit.id}")
    }

    /** Delete a habit and all its history */
    suspend fun deleteHabit(habitId: String) {
        // Delete the habit
        habitsCollection.document(habitId).delete().await()

        // Delete all history for this habit
        val historyDocs = historyCollection.whereEqualTo("habitId", habitId).get().await()

        for (doc in historyDocs.documents) {
            doc.reference.delete().await()
        }
        Log.d(TAG, "Deleted habit: $habitId and ${historyDocs.size()} history entries")
    }

    /** Update streak for a habit */
    suspend fun updateStreak(habitId: String, streak: Int) {
        habitsCollection.document(habitId).update("streak", streak).await()
        Log.d(TAG, "Updated streak for $habitId to $streak")
    }

    // ==================== HISTORY ====================

    /** Get all history as Flow (real-time updates) */
    val allHistory: Flow<List<HabitHistory>> = callbackFlow {
        val listenerRegistration: ListenerRegistration =
                historyCollection.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error fetching history", error)
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val history =
                            snapshot?.documents?.mapNotNull { doc ->
                                try {
                                    doc.toObject(HabitHistory::class.java)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error deserializing history", e)
                                    null
                                }
                            }
                                    ?: emptyList()

                    Log.d(TAG, "Fetched ${history.size} history entries")
                    trySend(history)
                }

        awaitClose { listenerRegistration.remove() }
    }

    /** Get history for a specific date */
    fun getHistoryForDate(date: Long): Flow<List<HabitHistory>> = callbackFlow {
        val listenerRegistration: ListenerRegistration =
                historyCollection.whereEqualTo("date", date).addSnapshotListener { snapshot, error
                    ->
                    if (error != null) {
                        Log.e(TAG, "Error fetching history for date", error)
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val history =
                            snapshot?.documents?.mapNotNull { doc ->
                                try {
                                    doc.toObject(HabitHistory::class.java)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error deserializing history", e)
                                    null
                                }
                            }
                                    ?: emptyList()

                    Log.d(TAG, "Fetched ${history.size} history entries for date $date")
                    trySend(history)
                }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Update habit progress for a specific date Creates new history entry if it doesn't exist, or
     * updates existing
     */
    suspend fun updateHabitProgress(habitId: String, date: Long, progress: Int, isDone: Boolean) {
        // Find existing history for this habit and date
        val existingDocs =
                historyCollection
                        .whereEqualTo("habitId", habitId)
                        .whereEqualTo("date", date)
                        .get()
                        .await()

        if (existingDocs.isEmpty) {
            // Create new history entry
            val docRef = historyCollection.document()
            val history =
                    HabitHistory(
                            id = docRef.id,
                            habitId = habitId,
                            date = date,
                            currentProgress = progress,
                            isDone = isDone
                    )
            docRef.set(history).await()
            Log.d(TAG, "Created history entry: ${docRef.id}, isDone=$isDone")
        } else {
            // Update existing
            val doc = existingDocs.documents.first()
            doc.reference.update(mapOf("currentProgress" to progress, "isDone" to isDone)).await()
            Log.d(TAG, "Updated history entry: ${doc.id}, isDone=$isDone")
        }
    }

    /** Get completed history for a habit (for streak calculation) */
    suspend fun getCompletedHistory(habitId: String): List<HabitHistory> {
        return try {
            val docs =
                    historyCollection
                            .whereEqualTo("habitId", habitId)
                            .whereEqualTo("isDone", true)
                            .get()
                            .await()

            docs.documents.mapNotNull {
                try {
                    it.toObject(HabitHistory::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting completed history", e)
            emptyList()
        }
    }

    /** Refresh streak for a habit Logic ported from Room-based HabitRepository */
    suspend fun refreshStreak(habitId: String) {
        val completedHistory = getCompletedHistory(habitId)
        val sortedDates = completedHistory.map { it.date }.sortedDescending().distinct()

        if (sortedDates.isEmpty()) {
            updateStreak(habitId, 0)
            return
        }

        val habit = getHabitById(habitId) ?: return
        val selectedDays = habit.selectedDays

        val today = java.time.LocalDate.now().toEpochDay()
        val lastCompletion = sortedDates.first()

        // Check if streak is broken
        var isStreakBroken = false
        for (d in (lastCompletion + 1) until today) {
            val dateObj = java.time.LocalDate.ofEpochDay(d)
            val dayOfWeek = dateObj.dayOfWeek.value % 7
            if (selectedDays[dayOfWeek]) {
                isStreakBroken = true
                break
            }
        }

        if (isStreakBroken) {
            updateStreak(habitId, 0)
            return
        }

        // Calculate streak length
        var currentStreak = 1
        var previousDate = lastCompletion

        for (i in 1 until sortedDates.size) {
            val currentDate = sortedDates[i]

            var gapBroken = false
            for (d in (currentDate + 1) until previousDate) {
                val dateObj = java.time.LocalDate.ofEpochDay(d)
                val dayOfWeek = dateObj.dayOfWeek.value % 7
                if (selectedDays[dayOfWeek]) {
                    gapBroken = true
                    break
                }
            }

            if (gapBroken) {
                break
            } else {
                currentStreak++
                previousDate = currentDate
            }
        }

        updateStreak(habitId, currentStreak)
    }

    /** Reset daily progress - kept for compatibility */
    suspend fun resetDailyProgress() {
        // No-op: History is date-based, no need to reset
    }
}
