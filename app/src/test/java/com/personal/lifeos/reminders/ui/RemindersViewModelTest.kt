package com.personal.lifeos.reminders.ui

import app.cash.turbine.test
import com.personal.lifeos.ai.AIService
import com.personal.lifeos.ai.model.TaskParsingResult
import com.personal.lifeos.core.data.local.dao.TaskDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class RemindersViewModelTest {

    private val mockTaskDao = mockk<TaskDao>()
    private val mockAiService = mockk<AIService>()
    private lateinit var viewModel: RemindersViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { mockTaskDao.getAllTasks() } returns flowOf(emptyList())
        viewModel = RemindersViewModel(mockTaskDao, mockAiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processInput updates loading state and saves task on success`() = runTest {
        // Arrange
        val userInput = "Call mom"
        coEvery { mockAiService.parseInput(userInput) } returns Result.success(
            TaskParsingResult("Call mom", "Check in", "2026-10-15T09:00:00Z", "medium")
        )
        coEvery { mockTaskDao.insertTask(any()) } returns Unit

        // Act & Assert
        viewModel.isProcessing.test {
            assertEquals(false, awaitItem()) // Initial state
            
            viewModel.processInput(userInput)
            
            assertEquals(true, awaitItem())  // Becomes loading when processInput starts
            assertEquals(false, awaitItem()) // Goes back to false when done
        }
    }
}
