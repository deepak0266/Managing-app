package com.personal.lifeos.reminders.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.personal.lifeos.core.data.local.entity.TaskEntity
import com.personal.lifeos.ui.components.EmptyState
import com.personal.lifeos.ui.components.PremiumInputBar
import com.personal.lifeos.ui.components.PremiumTopBar
import com.personal.lifeos.ui.components.ShimmerListLoading
import com.personal.lifeos.ui.theme.LocalPremiumColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RemindersScreen(
    viewModel: RemindersViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    // Simulate initial load
    LaunchedEffect(tasks) {
        if (tasks.isNotEmpty() || isLoaded) isLoaded = true
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        isLoaded = true
    }

    val premiumColors = LocalPremiumColors.current

    // Categorize tasks
    val now = System.currentTimeMillis()
    val overdueTasks = tasks.filter { !it.isCompleted && it.dueDate < now }
    val activeTasks = tasks.filter { !it.isCompleted && it.dueDate >= now }
    val completedTasks = tasks.filter { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Premium Top Bar with Greeting ──
        PremiumTopBar(
            title = "Life OS",
            showGreeting = true
        )

        // ── Content ──
        Box(modifier = Modifier.weight(1f)) {
            if (!isLoaded) {
                ShimmerListLoading(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            } else if (tasks.isEmpty()) {
                EmptyState(
                    emoji = "✨",
                    title = "No tasks yet",
                    subtitle = "Type or speak your first task.\nI'll understand natural language!",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // ── Overdue Section ──
                    if (overdueTasks.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "⚠\uFE0F Overdue",
                                color = premiumColors.errorColor
                            )
                        }
                        items(overdueTasks, key = { it.id }) { task ->
                            PremiumTaskCard(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                accentColor = premiumColors.errorColor
                            )
                        }
                    }

                    // ── Active Tasks ──
                    if (activeTasks.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "📋 Active",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(activeTasks, key = { it.id }) { task ->
                            PremiumTaskCard(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                accentColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // ── Completed ──
                    if (completedTasks.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "✅ Completed",
                                color = premiumColors.successColor
                            )
                        }
                        items(completedTasks, key = { it.id }) { task ->
                            PremiumTaskCard(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                accentColor = premiumColors.successColor,
                                isCompletedStyle = true
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }

        // ── Premium Input Bar ──
        PremiumInputBar(
            inputText = inputText,
            onInputChanged = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    viewModel.addTaskFromNaturalLanguage(inputText)
                    inputText = ""
                }
            },
            isProcessing = isProcessing,
            placeholder = "e.g. Remind me to take medicine at 8pm",
            suggestions = listOf("💊 Medicine", "💧 Water", "🏋️ Workout")
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun PremiumTaskCard(
    task: TaskEntity,
    onToggleCompletion: () -> Unit,
    accentColor: Color,
    isCompletedStyle: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                isPressed = true
                onToggleCompletion()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompletedStyle)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Accent Color Strip ──
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ── Check Icon ──
            val checkScale by animateFloatAsState(
                targetValue = if (task.isCompleted) 1.1f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "check_scale"
            )

            Icon(
                imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = "Toggle Complete",
                tint = if (task.isCompleted) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(28.dp)
                    .scale(checkScale)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ── Task Content ──
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = if (isCompletedStyle) 0.5f else 1f
                    ),
                    textDecoration = if (isCompletedStyle) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(4.dp))

                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                val timeString = dateFormat.format(Date(task.dueDate))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "• $timeString",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (isCompletedStyle) 0.4f else 0.7f
                        )
                    )
                }

                if (task.recurringPattern != null) {
                    Text(
                        text = "🔁 ${task.recurringPattern}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary.copy(
                            alpha = if (isCompletedStyle) 0.4f else 0.8f
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    // Reset press state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}
