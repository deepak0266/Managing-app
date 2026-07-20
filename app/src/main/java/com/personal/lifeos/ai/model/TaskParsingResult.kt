package com.personal.lifeos.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskParsingResult(
    val title: String,
    val category: String, // medicine, water, workout, bill, general
    val timeToRemindMs: Long?, // explicit time or null
    val recurringPattern: String? // e.g., "every 3 hours"
)
