package com.personal.lifeos.expenses.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.personal.lifeos.core.data.local.entity.ExpenseEntity
import com.personal.lifeos.ui.components.EmptyState
import com.personal.lifeos.ui.components.PremiumInputBar
import com.personal.lifeos.ui.components.PremiumTopBar
import com.personal.lifeos.ui.components.ShimmerChartLoading
import com.personal.lifeos.ui.components.ShimmerListLoading
import com.personal.lifeos.ui.theme.CategoryColors
import com.personal.lifeos.ui.theme.LocalPremiumColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.expenses.collectAsState()
    val weeklyChartData by viewModel.weeklyChartData.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(expenses) {
        if (expenses.isNotEmpty() || isLoaded) isLoaded = true
    }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        isLoaded = true
    }

    val premiumColors = LocalPremiumColors.current

    // Calculate today's total spending
    val todayTotal = remember(expenses) {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        expenses.filter {
            it.transactionType == "expense" && it.date >= todayStart
        }.sumOf { it.amount }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Premium Top Bar ──
        PremiumTopBar(title = "Money Memory")

        // ── Content ──
        Box(modifier = Modifier.weight(1f)) {
            if (!isLoaded) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ShimmerChartLoading()
                    ShimmerListLoading(itemCount = 3)
                }
            } else if (expenses.isEmpty()) {
                EmptyState(
                    emoji = "💰",
                    title = "No transactions yet",
                    subtitle = "Tell me what you spent.\nI'll categorize it automatically!",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Today's Summary Card ──
                    item {
                        TodaySummaryCard(todayTotal = todayTotal)
                    }

                    // ── Weekly Chart ──
                    item {
                        PremiumChartCard(weeklyChartData)
                    }

                    // ── Recent Transactions ──
                    item {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(expenses) { expense ->
                        PremiumTransactionCard(expense = expense)
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
                    viewModel.addExpenseFromNaturalLanguage(inputText)
                    inputText = ""
                }
            },
            isProcessing = isProcessing,
            placeholder = "e.g. Spent ₹350 on groceries via UPI",
            suggestions = listOf("🍔 Food", "🚗 Travel", "🛒 Shopping")
        )
    }
}

@Composable
private fun TodaySummaryCard(todayTotal: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Today's Spending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Animated counter
                val animatedTotal by animateFloatAsState(
                    targetValue = todayTotal.toFloat(),
                    animationSpec = tween(800, easing = FastOutSlowInEasing),
                    label = "total_anim"
                )

                Text(
                    text = "₹${String.format("%.0f", animatedTotal)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun PremiumChartCard(weeklyChartData: List<Pair<String, Double>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (weeklyChartData.isNotEmpty()) {
                val chartEntryModel = entryModelOf(*(weeklyChartData.map { it.second }.toTypedArray()))
                Chart(
                    chart = columnChart(),
                    model = chartEntryModel,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { value, _ ->
                            val index = value.toInt()
                            if (index >= 0 && index < weeklyChartData.size) {
                                weeklyChartData[index].first
                            } else ""
                        }
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No spending data this week",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumTransactionCard(expense: ExpenseEntity) {
    val isExpense = expense.transactionType.lowercase() == "expense"
    val amountColor = if (isExpense) LocalPremiumColors.current.errorColor else LocalPremiumColors.current.successColor
    val prefix = if (isExpense) "-" else "+"

    // Get category emoji and color
    val categoryEmoji = getCategoryEmoji(expense.category)
    val categoryColor = CategoryColors[expense.category.lowercase()] ?: MaterialTheme.colorScheme.primaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Category Emoji Icon ──
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = categoryEmoji,
                    fontSize = 22.sp
                )
            }

            // ── Transaction Details ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                val timeString = dateFormat.format(Date(expense.date))

                Text(
                    text = "${expense.category.replaceFirstChar { it.uppercase() }} • $timeString",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Amount ──
            Text(
                text = "$prefix${expense.currency} ${expense.amount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

private fun getCategoryEmoji(category: String): String {
    return when (category.lowercase()) {
        "food" -> "🍔"
        "travel" -> "🚗"
        "shopping" -> "🛒"
        "subscription" -> "📱"
        "health" -> "💊"
        "entertainment" -> "🎬"
        "bills" -> "🧾"
        "salary" -> "💰"
        "education" -> "📚"
        "rent" -> "🏠"
        else -> "💳"
    }
}
