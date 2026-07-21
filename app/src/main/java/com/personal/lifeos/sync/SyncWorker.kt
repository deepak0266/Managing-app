package com.personal.lifeos.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.personal.lifeos.BuildConfig
import com.personal.lifeos.core.data.local.dao.ExpenseDao
import com.personal.lifeos.core.data.local.dao.HabitDao
import com.personal.lifeos.core.data.local.dao.TaskDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import java.util.UUID

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskDao: TaskDao,
    private val expenseDao: ExpenseDao,
    private val habitDao: HabitDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_ANON_KEY

        if (supabaseUrl.isBlank() || supabaseKey.isBlank()) {
            return Result.failure()
        }

        try {
            // Retrieve or generate a unique Device ID for server-side categorization
            val prefs = applicationContext.getSharedPreferences("lifeos_sync_prefs", android.content.Context.MODE_PRIVATE)
            var deviceId = prefs.getString("device_id", null)
            if (deviceId == null) {
                deviceId = UUID.randomUUID().toString()
                prefs.edit().putString("device_id", deviceId).apply()
            }

            val client = createSupabaseClient(
                supabaseUrl = supabaseUrl,
                supabaseKey = supabaseKey
            ) {
                install(Postgrest)
            }

            // Snapshot local data
            val tasks = taskDao.getAllTasks().first()
            val expenses = expenseDao.getAllExpenses().first()
            val habits = habitDao.getAllHabits().first()

            // Map local entities to Sync DTOs containing the device_id
            val tasksDto = tasks.map { 
                TaskSyncDto(it.id, it.title, it.isCompleted, it.dueDate, it.category, it.recurringPattern, deviceId) 
            }
            val expensesDto = expenses.map { 
                ExpenseSyncDto(it.id, it.amount, it.category, it.description, it.date, it.transactionType, it.currency, deviceId) 
            }
            val habitsDto = habits.map { 
                HabitSyncDto(it.id, it.name, it.targetDaysPerWeek, it.currentStreak, it.isVacationModeActive, deviceId) 
            }

            // Push to Supabase (Upsert based on id)
            // Assumes tables "tasks", "expenses", and "habits" exist on Supabase with matching schema + device_id column
            if (tasksDto.isNotEmpty()) client.postgrest["tasks"].upsert(tasksDto)
            if (expensesDto.isNotEmpty()) client.postgrest["expenses"].upsert(expensesDto)
            if (habitsDto.isNotEmpty()) client.postgrest["habits"].upsert(habitsDto)

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}

// DTOs for Supabase Sync (Includes device_id for server-side categorization)

@Serializable
data class TaskSyncDto(
    val id: Long, val title: String, val isCompleted: Boolean, 
    val dueDate: Long, val category: String, val recurringPattern: String?, 
    val device_id: String
)

@Serializable
data class ExpenseSyncDto(
    val id: Long, val amount: Double, val category: String, 
    val description: String, val date: Long, val transactionType: String, 
    val currency: String, val device_id: String
)

@Serializable
data class HabitSyncDto(
    val id: Long, val name: String, val targetDaysPerWeek: Int, 
    val currentStreak: Int, val isVacationModeActive: Boolean, 
    val device_id: String
)
