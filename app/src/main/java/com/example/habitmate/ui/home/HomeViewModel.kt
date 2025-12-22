package com.example.habitmate.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.habitmate.HabitMateApplication
import com.example.habitmate.data.local.HabitEntity
import com.example.habitmate.data.repository.HabitRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    // 1. All Habits (For Master Page) & History Trigger
    private val _selectedDate = kotlinx.coroutines.flow.MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val allHabits: StateFlow<List<HabitUi>> =
            repository
                    .allHabits
                    .map { entities ->
                        entities.map { entity ->
                            HabitUi(
                                    id = entity.id,
                                    title = entity.title,
                                    emoji = entity.emoji,
                                    timeOfDay = entity.timeOfDay,
                                    unitLabel = entity.unitLabel,
                                    current = entity.current, // Base default
                                    target = entity.target,
                                    isDoneToday = entity.isDoneToday, // Base default
                                    streak = entity.streak,
                                    selectedDays = entity.selectedDays,
                                    weeklyTarget = entity.weeklyTarget
                            )
                        }
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = WhileSubscribed(5000),
                            initialValue = emptyList()
                    )

    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        // No need to fetch manually, Flow will trigger
    }

    // 3. Animation State
    private val _hasAnimated = kotlinx.coroutines.flow.MutableStateFlow(false)
    val hasAnimated: StateFlow<Boolean> = _hasAnimated.asStateFlow()

    fun setAnimated() {
        _hasAnimated.value = true
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<List<HabitUi>> =
            combine(allHabits, _selectedDate) { habits, date ->
                        Triple(habits, date, repository.getHistoryForDate(date.toEpochDay()))
                    }
                    .flatMapLatest { (habits, date, historyFlow) ->
                        historyFlow.map { historyList ->
                            val dayOfWeek = date.dayOfWeek.value % 7
                            habits
                                    .filter { it.selectedDays[dayOfWeek] || it.weeklyTarget > 0 }
                                    .map { habit ->
                                        // OVERRIDE with History Data
                                        val history = historyList.find { it.habitId == habit.id }
                                        habit.copy(
                                                current = history?.currentProgress ?: 0,
                                                isDoneToday = history?.isDone ?: false
                                        )
                                    }
                        }
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = WhileSubscribed(5000),
                            initialValue = emptyList()
                    )

    fun addHabit(
            title: String,
            emoji: String,
            target: Int,
            unit: String,
            timeOfDay: HabitTimeOfDay,
            selectedDays: List<Boolean>,
            weeklyTarget: Int
    ) {
        viewModelScope.launch {
            repository.insertHabit(
                    HabitEntity(
                            title = title,
                            emoji = emoji,
                            target = target,
                            unitLabel = unit,
                            timeOfDay = timeOfDay,
                            selectedDays = selectedDays,
                            weeklyTarget = weeklyTarget
                    )
            )
        }
    }

    fun updateHabit(updatedHabit: HabitUi) {
        viewModelScope.launch {
            repository.updateHabit(
                    HabitEntity(
                            id = updatedHabit.id,
                            title = updatedHabit.title,
                            emoji = updatedHabit.emoji,
                            target = updatedHabit.target,
                            unitLabel = updatedHabit.unitLabel,
                            timeOfDay = updatedHabit.timeOfDay,
                            selectedDays = updatedHabit.selectedDays,
                            weeklyTarget = updatedHabit.weeklyTarget,
                            current = updatedHabit.current,
                            isDoneToday = updatedHabit.isDoneToday,
                            streak = updatedHabit.streak
                    )
            )
        }
    }

    fun toggleHabit(id: Int) {
        val currentDate = _selectedDate.value
        val historyList = uiState.value // Use current UI state which includes history overrides
        val habit = historyList.find { it.id == id } ?: return

        viewModelScope.launch {
            if (habit.current < habit.target) {
                val newCurrent = habit.current + 1
                val isDone = newCurrent >= habit.target

                // Update History Table
                repository.updateHabitProgress(
                        habitId = habit.id,
                        date = currentDate.toEpochDay(),
                        progress = newCurrent,
                        isDone = isDone
                )

                // Logic for Streak optimization (Optional, can be added later or now)
                // For now just fix the visual bug
            }
        }
    }

    fun resetHabit(id: Int) {
        val currentDate = _selectedDate.value
        val historyList = uiState.value
        val habit = historyList.find { it.id == id } ?: return

        viewModelScope.launch {
            if (habit.current > 0) {
                val newCurrent = habit.current - 1
                val isDone = newCurrent >= habit.target

                repository.updateHabitProgress(
                        habitId = habit.id,
                        date = currentDate.toEpochDay(),
                        progress = newCurrent,
                        isDone = isDone
                )
            }
        }
    }

    fun deleteHabit(habit: HabitUi) {
        viewModelScope.launch {
            val entity =
                    HabitEntity(
                            id = habit.id,
                            title = habit.title,
                            emoji = habit.emoji,
                            timeOfDay = habit.timeOfDay,
                            unitLabel = habit.unitLabel,
                            current = habit.current,
                            target = habit.target,
                            isDoneToday = habit.isDoneToday,
                            streak = habit.streak,
                            selectedDays = habit.selectedDays
                    )
            repository.deleteHabit(entity)
        }
    }

    // Define ViewModel factory in a companion object
    companion object {
        val Factory: ViewModelProvider.Factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(
                            modelClass: Class<T>,
                            extras: CreationExtras
                    ): T {
                        // Get the Application object from extras
                        val application = checkNotNull(extras[APPLICATION_KEY])
                        // Create a SavedStateHandle for this ViewModel (if needed) like:
                        // val savedStateHandle = extras.createSavedStateHandle()

                        return HomeViewModel((application as HabitMateApplication).repository) as T
                    }
                }
    }
}
