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

            // Push to Supabase (Upsert based on id)
            // Assumes tables "tasks", "expenses", and "habits" exist on Supabase with matching schema
            if (tasks.isNotEmpty()) client.postgrest["tasks"].upsert(tasks)
            if (expenses.isNotEmpty()) client.postgrest["expenses"].upsert(expenses)
            if (habits.isNotEmpty()) client.postgrest["habits"].upsert(habits)

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}
