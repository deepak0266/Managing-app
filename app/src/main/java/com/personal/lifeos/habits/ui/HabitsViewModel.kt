package com.personal.lifeos.habits.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.lifeos.core.data.local.dao.HabitDao
import com.personal.lifeos.core.data.local.entity.HabitEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitDao: HabitDao
) : ViewModel() {

    val habits: StateFlow<List<HabitEntity>> = habitDao.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createHabit(name: String, category: String = "general", targetDaysPerWeek: Int = 7) {
        viewModelScope.launch {
            val habit = HabitEntity(
                name = name,
                category = category,
                frequency = "weekly",
                targetDaysPerWeek = targetDaysPerWeek
            )
            habitDao.insertHabit(habit)
        }
    }

    fun toggleVacationMode(habit: HabitEntity) {
        viewModelScope.launch {
            habitDao.updateHabit(habit.copy(isVacationModeActive = !habit.isVacationModeActive))
        }
    }

    fun logHabitCompletion(habit: HabitEntity) {
        // Simplified streak logic: +1 streak on click.
        // A real implementation would verify if it was already clicked today.
        viewModelScope.launch {
            val newStreak = habit.currentStreak + 1
            val newLongest = maxOf(newStreak, habit.longestStreak)
            habitDao.updateHabit(
                habit.copy(
                    currentStreak = newStreak,
                    longestStreak = newLongest
                )
            )
        }
    }
}
