package com.personal.lifeos.core.di

import android.content.Context
import androidx.room.Room
import com.personal.lifeos.core.data.local.LifeOSDatabase
import com.personal.lifeos.core.data.local.dao.ExpenseDao
import com.personal.lifeos.core.data.local.dao.HabitDao
import com.personal.lifeos.core.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import net.sqlcipher.database.SupportFactory
import com.personal.lifeos.core.data.local.KeyStoreHelper
import net.sqlcipher.database.SQLiteDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLifeOSDatabase(
        @ApplicationContext context: Context
    ): LifeOSDatabase {
        // SQLCipher init (required for older versions, though modern ones often do it automatically, good practice to ensure)
        System.loadLibrary("sqlcipher")
        
        val passphrase = KeyStoreHelper.getOrGenerateDatabasePassphrase().toByteArray()
        val supportFactory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            LifeOSDatabase::class.java,
            LifeOSDatabase.DATABASE_NAME
        )
        .openHelperFactory(supportFactory)
        .fallbackToDestructiveMigration() // For MVP purposes, clear DB if schema changes
        .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: LifeOSDatabase): TaskDao {
        return database.taskDao
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: LifeOSDatabase): ExpenseDao {
        return database.expenseDao
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: LifeOSDatabase): HabitDao {
        return database.habitDao
    }
}
