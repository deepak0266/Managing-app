package com.personal.lifeos.ai

import com.personal.lifeos.ai.model.TaskParsingResult

interface AIService {
    /**
     * Parses a natural language sentence into a structured task.
     * e.g. "Remind me to drink water every 3 hours"
     */
    suspend fun parseTask(naturalLanguageInput: String): Result<TaskParsingResult>
    
    /**
     * Parses a natural language sentence into a structured expense.
     * e.g. "Paid 350 at Starbucks for coffee"
     */
    suspend fun parseExpense(naturalLanguageInput: String): Result<com.personal.lifeos.ai.model.ExpenseParsingResult>
    
    /**
     * Queries the AI Memory Engine using local context.
     */
    suspend fun queryMemory(question: String, contextData: String): Result<String>
}
