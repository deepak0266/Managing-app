package com.personal.lifeos.reminders.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.personal.lifeos.R
import com.personal.lifeos.core.data.local.dao.TaskDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val taskDao: TaskDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val taskId = inputData.getLong("TASK_ID", -1)
        if (taskId == -1L) return@withContext Result.failure()

        val task = taskDao.getTaskById(taskId) ?: return@withContext Result.failure()

        if (task.isCompleted) return@withContext Result.success()

        showNotification(task.title, task.description ?: "It's time for your task!")
        
        // Handle adaptive backoff / recurrence here in later phases
        
        Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "lifeos_reminders_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Smart Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for AI scheduled reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            // Using default icon since we don't have a drawable yet. A real app will use R.drawable.ic_notification
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, android.R.color.holo_blue_light))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
