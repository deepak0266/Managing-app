package com.personal.lifeos.ai

import com.personal.lifeos.ai.model.TaskParsingResult
import com.personal.lifeos.ai.model.ExpenseParsingResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class OpenAIServiceTest {

    private val mockApi = mockk<OpenAIApi>()
    private val service = OpenAIService(mockApi)

    @Test
    fun `parseInput returns Task type when AI detects a reminder`() = runTest {
        // Arrange
        val mockJsonResponse = """
            {
              "choices": [
                {
                  "message": {
                    "content": "{\"type\":\"task\",\"task\":{\"title\":\"Buy milk\",\"description\":\"from the store\",\"dueDate\":\"2026-10-15T09:00:00Z\",\"priority\":\"high\"}}"
                  }
                }
              ]
            }
        """.trimIndent()
        
        val responseBody = mockJsonResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val fakeResponse = Response.success(
            OpenAIResponse(
                choices = listOf(Choice(Message("assistant", "{\"type\":\"task\",\"task\":{\"title\":\"Buy milk\",\"description\":\"from the store\",\"dueDate\":\"2026-10-15T09:00:00Z\",\"priority\":\"high\"}}")))
            )
        )
        coEvery { mockApi.getChatCompletion(any()) } returns fakeResponse

        // Act
        val result = service.parseInput("Remind me to buy milk from the store tomorrow at 9am")

        // Assert
        assertTrue(result.isSuccess)
        val parsed = result.getOrNull()
        assertTrue(parsed is TaskParsingResult)
        
        val taskResult = parsed as TaskParsingResult
        assertEquals("Buy milk", taskResult.title)
        assertEquals("high", taskResult.priority)
    }
}
