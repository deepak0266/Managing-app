package com.personal.lifeos.reminders.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.personal.lifeos.ai.AIService
import com.personal.lifeos.core.data.local.dao.TaskDao
import com.personal.lifeos.core.data.local.entity.TaskEntity
import com.personal.lifeos.reminders.worker.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val aiService: AIService,
    private val taskDao: TaskDao,
    private val workManager: WorkManager
) : ViewModel() {

    val tasks: StateFlow<List<TaskEntity>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    fun addTaskFromNaturalLanguage(input: String) {
        if (input.isBlank()) return

        viewModelScope.launch {
            _isProcessing.value = true
            val result = aiService.parseTask(input)
            
            result.onSuccess { parsedData ->
                val entity = TaskEntity(
                    title = parsedData.title,
                    category = parsedData.category,
                    dueDate = parsedData.timeToRemindMs ?: (System.currentTimeMillis() + 3600000), // default to 1 hr if null
                    recurringPattern = parsedData.recurringPattern
                )
                val taskId = taskDao.insertTask(entity)
                scheduleReminderWorker(taskId, entity.dueDate)
            }
            
            _isProcessing.value = false
        }
    }

    private fun scheduleReminderWorker(taskId: Long, timeMs: Long) {
        val delay = timeMs - System.currentTimeMillis()
        if (delay <= 0) return // Already past

        val data = Data.Builder().putLong("TASK_ID", taskId).build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueue(request)
    }
    
    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }
}
