package com.personal.lifeos.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val currency: String = "INR",
    val description: String,
    val category: String, // food, travel, subscription, etc.
    val paymentMode: String, // cash, upi, card
    val transactionType: String, // expense, income, borrow, lend
    val date: Long = System.currentTimeMillis(),
    val merchant: String? = null,
    val isRecurring: Boolean = false
)
