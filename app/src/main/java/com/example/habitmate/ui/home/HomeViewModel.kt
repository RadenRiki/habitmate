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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    // 1. All Habits (For Master Page)
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
                                    current = entity.current,
                                    target = entity.target,
                                    isDoneToday = entity.isDoneToday,
                                    streak = entity.streak,
                                    selectedDays = entity.selectedDays
                            )
                        }
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = WhileSubscribed(5000),
                            initialValue = emptyList()
                    )

    // 3. Animation State (Single Event)
    private val _hasAnimated = kotlinx.coroutines.flow.MutableStateFlow(false)
    val hasAnimated: StateFlow<Boolean> = _hasAnimated.asStateFlow()

    fun setAnimated() {
        _hasAnimated.value = true
    }

    // 2. Today's Habits (For Home Screen)
    val uiState: StateFlow<List<HabitUi>> =
            allHabits
                    .map { habits ->
                        val todayDayOfWeek = LocalDate.now().dayOfWeek.value % 7 // 0=Sun, 1=Mon...
                        habits.filter {
                            // Show if specific day selected OR if it's a flexible weekly habit
                            // (weeklyTarget > 0)
                            // Flexible habits show every day for now (simplified logic)
                            it.selectedDays[todayDayOfWeek] || it.weeklyTarget > 0
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
            weeklyTarget: Int // New param
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

    fun toggleHabit(id: Int) {
        // Use allHabits to find it, even if filtered out (though unlikely for toggle)
        val habit = allHabits.value.find { it.id == id } ?: return

        viewModelScope.launch {
            if (habit.current < habit.target) {
                val newCurrent = habit.current + 1
                val isDone = newCurrent >= habit.target
                val entity =
                        HabitEntity(
                                id = habit.id,
                                title = habit.title,
                                emoji = habit.emoji,
                                timeOfDay = habit.timeOfDay,
                                unitLabel = habit.unitLabel,
                                current = newCurrent,
                                target = habit.target,
                                isDoneToday = isDone,
                                streak = habit.streak,
                                selectedDays = habit.selectedDays
                        )
                repository.updateHabit(entity)
            }
        }
    }

    fun resetHabit(id: Int) {
        val habit = allHabits.value.find { it.id == id } ?: return

        viewModelScope.launch {
            if (habit.current > 0) {
                val newCurrent = habit.current - 1
                val isDone = newCurrent >= habit.target
                val entity =
                        HabitEntity(
                                id = habit.id,
                                title = habit.title,
                                emoji = habit.emoji,
                                timeOfDay = habit.timeOfDay,
                                unitLabel = habit.unitLabel,
                                current = newCurrent,
                                target = habit.target,
                                isDoneToday = isDone,
                                streak = habit.streak,
                                selectedDays = habit.selectedDays
                        )
                repository.updateHabit(entity)
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
