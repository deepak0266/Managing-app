package com.personal.lifeos.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.personal.lifeos.core.data.local.dao.ExpenseDao
import com.personal.lifeos.core.data.local.dao.HabitDao
import com.personal.lifeos.core.data.local.dao.TaskDao
import com.personal.lifeos.core.data.local.entity.ExpenseEntity
import com.personal.lifeos.core.data.local.entity.HabitEntity
import com.personal.lifeos.core.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, ExpenseEntity::class, HabitEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LifeOSDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val expenseDao: ExpenseDao
    abstract val habitDao: HabitDao
    
    companion object {
        const val DATABASE_NAME = "lifeos_db"
    }
}
