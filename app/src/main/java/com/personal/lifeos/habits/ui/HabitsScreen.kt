package com.personal.lifeos.habits.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.personal.lifeos.core.data.local.entity.HabitEntity
import com.personal.lifeos.ui.components.EmptyState
import com.personal.lifeos.ui.components.PremiumTopBar
import com.personal.lifeos.ui.components.ShimmerListLoading
import com.personal.lifeos.ui.theme.LocalPremiumColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    viewModel: HabitsViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(habits) {
        if (habits.isNotEmpty() || isLoaded) isLoaded = true
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        isLoaded = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Premium Top Bar ──
        PremiumTopBar(
            title = "Habits",
            actions = {
                FilledTonalIconButton(
                    onClick = { showAddSheet = true },
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Habit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // ── Content ──
        Box(modifier = Modifier.weight(1f)) {
            if (!isLoaded) {
                ShimmerListLoading(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            } else if (habits.isEmpty()) {
                EmptyState(
                    emoji = "🏋️",
                    title = "No habits yet",
                    subtitle = "Start building habits that stick.\nTrack your streaks and stay consistent!",
                    ctaText = "Create First Habit",
                    onCtaClick = { showAddSheet = true },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(habits, key = { it.id }) { habit ->
                        PremiumHabitCard(
                            habit = habit,
                            onLogCompletion = { viewModel.logHabitCompletion(habit) },
                            onToggleVacation = { viewModel.toggleVacationMode(habit) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    // ── Add Habit Bottom Sheet ──
    if (showAddSheet) {
        AddHabitBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { name ->
                viewModel.createHabit(name)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun PremiumHabitCard(
    habit: HabitEntity,
    onLogCompletion: () -> Unit,
    onToggleVacation: () -> Unit
) {
    val premiumColors = LocalPremiumColors.current

    // Calculate weekly progress (streak % targetDaysPerWeek)
    val weeklyProgress = if (habit.targetDaysPerWeek > 0) {
        minOf(
            (habit.currentStreak % habit.targetDaysPerWeek).toFloat() / habit.targetDaysPerWeek,
            1f
        ).let { if (it == 0f && habit.currentStreak > 0) 1f else it }
    } else 0f

    // Flame scale animation for high streaks
    val flameScale by animateFloatAsState(
        targetValue = when {
            habit.currentStreak >= 30 -> 1.3f
            habit.currentStreak >= 7 -> 1.15f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "flame_scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Circular Progress Ring ──
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp)
            ) {
                CircularProgressRing(
                    progress = weeklyProgress,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 5.dp,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    progressColor = MaterialTheme.colorScheme.primary
                )

                // Streak number inside ring
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = premiumColors.streakFlame,
                        modifier = Modifier
                            .size(18.dp)
                            .scale(flameScale)
                    )
                    Text(
                        text = "${habit.currentStreak}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = premiumColors.streakFlame
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ── Habit Info ──
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Target: ${habit.targetDaysPerWeek} days/week",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Action Buttons Row ──
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Done button
                    var isDonePressed by remember { mutableStateOf(false) }
                    val doneScale by animateFloatAsState(
                        targetValue = if (isDonePressed) 0.9f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "done_scale"
                    )

                    Button(
                        onClick = {
                            isDonePressed = true
                            onLogCompletion()
                        },
                        enabled = !habit.isVacationModeActive,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.scale(doneScale),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            "Done ✓",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Vacation mode toggle
                    IconButton(
                        onClick = onToggleVacation,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (habit.isVacationModeActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AirplanemodeActive,
                            contentDescription = "Vacation Mode",
                            tint = if (habit.isVacationModeActive)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Reset done press state
                    LaunchedEffect(isDonePressed) {
                        if (isDonePressed) {
                            kotlinx.coroutines.delay(200)
                            isDonePressed = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CircularProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    trackColor: Color = Color.Gray.copy(alpha = 0.2f),
    progressColor: Color = Color.Blue
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "ring_progress"
    )

    Canvas(modifier = modifier) {
        val sweepAngle = animatedProgress * 360f
        val strokePx = strokeWidth.toPx()
        val arcSize = Size(size.width - strokePx, size.height - strokePx)
        val topLeft = androidx.compose.ui.geometry.Offset(strokePx / 2, strokePx / 2)

        // Track
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokePx, cap = StrokeCap.Round),
            size = arcSize,
            topLeft = topLeft
        )

        // Progress
        if (sweepAngle > 0f) {
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHabitBottomSheet(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "New Habit",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Build a habit that sticks. Start small!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit name") },
                placeholder = { Text("e.g. Read 10 pages") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) onSave(name)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = name.isNotBlank(),
                contentPadding = PaddingValues(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Start Building 🚀",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
