package com.personal.lifeos.expenses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.lifeos.ai.AIService
import com.personal.lifeos.core.data.local.dao.ExpenseDao
import com.personal.lifeos.core.data.local.entity.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val aiService: AIService,
    private val expenseDao: ExpenseDao
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Group expenses by day for the last 7 days for charting
    val weeklyChartData: StateFlow<List<Pair<String, Double>>> = expenses.map { list ->
        val calendar = Calendar.getInstance()
        val data = mutableListOf<Pair<String, Double>>()
        
        // Go back 6 days + today = 7 days
        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            
            val startOfDay = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val endOfDay = startOfDay + 86399999L
            
            val dayTotal = list.filter { 
                it.transactionType == "expense" && it.date in startOfDay..endOfDay 
            }.sumOf { it.amount }
            
            val dayLabel = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, java.util.Locale.getDefault()) ?: ""
            data.add(Pair(dayLabel, dayTotal))
        }
        data
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    fun addExpenseFromNaturalLanguage(input: String) {
        if (input.isBlank()) return

        viewModelScope.launch {
            _isProcessing.value = true
            val result = aiService.parseExpense(input)
            
            result.onSuccess { parsedData ->
                val entity = ExpenseEntity(
                    amount = parsedData.amount,
                    currency = parsedData.currency,
                    description = parsedData.description,
                    category = parsedData.category,
                    paymentMode = parsedData.paymentMode,
                    transactionType = parsedData.transactionType,
                    merchant = parsedData.merchant,
                    isRecurring = parsedData.isRecurring,
                    date = System.currentTimeMillis()
                )
                expenseDao.insertExpense(entity)
            }
            
            _isProcessing.value = false
        }
    }
}
