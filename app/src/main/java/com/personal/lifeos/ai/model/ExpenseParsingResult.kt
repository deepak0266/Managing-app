package com.personal.lifeos.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpenseParsingResult(
    val amount: Double,
    val currency: String = "INR",
    val description: String,
    val category: String, // food, travel, subscription, entertainment, utilities, etc.
    val paymentMode: String, // cash, upi, card, unknown
    val transactionType: String, // expense, income, borrow, lend
    val merchant: String? = null,
    val isRecurring: Boolean = false
)
