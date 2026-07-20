package com.personal.lifeos.memory.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.lifeos.ai.AIService
import com.personal.lifeos.core.data.local.dao.ExpenseDao
import com.personal.lifeos.core.data.local.dao.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(val text: String, val isUser: Boolean)

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val aiService: AIService,
    private val expenseDao: ExpenseDao,
    private val taskDao: TaskDao
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    init {
        // Initial greeting
        _messages.value = listOf(
            ChatMessage("Hello! Ask me anything about your past tasks or expenses.", false)
        )
    }

    fun askQuestion(question: String) {
        if (question.isBlank()) return

        // Add user message
        _messages.value = _messages.value + ChatMessage(question, true)
        
        viewModelScope.launch {
            _isProcessing.value = true
            
            // Gather context data (simplistic approach: just grab all recent expenses/tasks)
            // In a production app, we would use FTS (Full Text Search) or embeddings to find relevant rows,
            // but for a personal app with small data size, sending recent data is fine.
            val expenses = expenseDao.getAllExpenses().first().take(50) // Last 50 expenses
            val tasks = taskDao.getAllTasks().first().take(50) // Next 50 tasks
            
            val contextBuilder = StringBuilder()
            contextBuilder.append("Expenses:\n")
            expenses.forEach { 
                contextBuilder.append("- ${it.date}: ${it.description} (${it.amount} ${it.currency}) Category: ${it.category}\n") 
            }
            contextBuilder.append("\nTasks:\n")
            tasks.forEach { 
                contextBuilder.append("- ${it.title} (Category: ${it.category}, Completed: ${it.isCompleted})\n") 
            }
            
            val result = aiService.queryMemory(question, contextBuilder.toString())
            
            result.onSuccess { answer ->
                _messages.value = _messages.value + ChatMessage(answer, false)
            }.onFailure {
                _messages.value = _messages.value + ChatMessage("Sorry, I had trouble finding that information.", false)
            }
            
            _isProcessing.value = false
        }
    }
}
