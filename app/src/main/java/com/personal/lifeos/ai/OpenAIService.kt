package com.personal.lifeos.ai

import com.google.gson.Gson
import com.personal.lifeos.ai.model.TaskParsingResult
import javax.inject.Inject

class OpenAIService @Inject constructor(
    private val openAIApi: OpenAIApi,
    private val gson: Gson
) : AIService {

    override suspend fun parseTask(naturalLanguageInput: String): Result<TaskParsingResult> {
        return try {
            val systemPrompt = """
                You are a personal AI assistant on an Android phone.
                Your job is to parse the user's natural language input into a structured JSON task.
                The JSON must exactly match this structure:
                {
                  "title": "String - clear short title of the task",
                  "category": "String - one of: medicine, water, workout, bill, sleep, meal, general",
                  "timeToRemindMs": "Long - Unix timestamp in milliseconds for explicit dates/times, or null if not explicitly stated",
                  "recurringPattern": "String - e.g., 'every 3 hours', 'daily', 'weekly' or null if one-time"
                }
            """.trimIndent()

            val request = OpenAIChatRequest(
                model = "gpt-4o-mini",
                messages = listOf(
                    OpenAIMessage(role = "system", content = systemPrompt),
                    OpenAIMessage(role = "user", content = naturalLanguageInput)
                ),
                response_format = ResponseFormat(type = "json_object")
            )

            val response = openAIApi.createChatCompletion(request)
            val jsonContent = response.choices.firstOrNull()?.message?.content
            
            if (jsonContent != null) {
                val parsedResult = gson.fromJson(jsonContent, TaskParsingResult::class.java)
                Result.success(parsedResult)
            } else {
                Result.failure(Exception("Failed to get valid content from OpenAI"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun parseExpense(naturalLanguageInput: String): Result<com.personal.lifeos.ai.model.ExpenseParsingResult> {
        return try {
            val systemPrompt = """
                You are a personal AI financial assistant on an Android phone.
                Your job is to parse the user's natural language input into a structured JSON expense or income record.
                The JSON must exactly match this structure:
                {
                  "amount": "Double - numeric amount, e.g. 350.0",
                  "currency": "String - e.g. 'INR', 'USD'",
                  "description": "String - short description",
                  "category": "String - e.g. food, travel, subscription, utilities, salary, transfer",
                  "paymentMode": "String - e.g. cash, upi, card, unknown",
                  "transactionType": "String - one of: expense, income, borrow, lend",
                  "merchant": "String - name of the person or business if known, else null",
                  "isRecurring": "Boolean - true if this looks like a subscription or recurring bill"
                }
            """.trimIndent()

            val request = OpenAIChatRequest(
                model = "gpt-4o-mini",
                messages = listOf(
                    OpenAIMessage(role = "system", content = systemPrompt),
                    OpenAIMessage(role = "user", content = naturalLanguageInput)
                ),
                response_format = ResponseFormat(type = "json_object")
            )

            val response = openAIApi.createChatCompletion(request)
            val jsonContent = response.choices.firstOrNull()?.message?.content
            
            if (jsonContent != null) {
                val parsedResult = gson.fromJson(jsonContent, com.personal.lifeos.ai.model.ExpenseParsingResult::class.java)
                Result.success(parsedResult)
            } else {
                Result.failure(Exception("Failed to get valid content from OpenAI"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun queryMemory(question: String, contextData: String): Result<String> {
        return try {
            val systemPrompt = """
                You are a personal AI Memory Engine on an Android phone.
                You are provided with context data from the user's local database.
                Answer the user's question concisely based ONLY on the provided context.
                If the answer cannot be found in the context, say "I don't have enough data to answer that yet."
                Do NOT output JSON. Output plain conversational text.
                
                CONTEXT DATA:
                $contextData
            """.trimIndent()

            val request = OpenAIChatRequest(
                model = "gpt-4o-mini",
                messages = listOf(
                    OpenAIMessage(role = "system", content = systemPrompt),
                    OpenAIMessage(role = "user", content = question)
                )
                // Note: No JSON response_format here
            )

            val response = openAIApi.createChatCompletion(request)
            val answer = response.choices.firstOrNull()?.message?.content
            
            if (answer != null) {
                Result.success(answer)
            } else {
                Result.failure(Exception("Failed to get valid response from OpenAI"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
