package com.personal.lifeos.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val category: String, // medicine, water, workout, bill, etc.
    val dueDate: Long, // timestamp
    val isCompleted: Boolean = false,
    val recurringPattern: String? = null, // e.g., "every 3 hours", "daily", "monthly"
    val nextFireTime: Long? = null, // Calculated next time to remind
    val ignoreCount: Int = 0, // For adaptive backoff
    val createdAt: Long = System.currentTimeMillis()
)
