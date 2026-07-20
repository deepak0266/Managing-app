package com.personal.lifeos.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.background
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.layout.height
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.layout.fillMaxWidth


class LifeOSWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In a complete implementation, we would query the Room DB here
        // to get the actual count of pending tasks and today's total expenses.
        val pendingTasksCount = 3
        val todayExpenses = 450.0

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color(0xFF0A0A0F))
                    .padding(20.dp),
                verticalAlignment = Alignment.Vertical.Top
            ) {
                // ── Header ──
                Text(
                    text = "⚡ Life OS",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(12.dp))

                // ── Tasks Summary ──
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = "📋",
                        style = TextStyle(color = ColorProvider(Color.White))
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = "$pendingTasksCount tasks pending",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF7C4DFF)),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(6.dp))

                // ── Expenses Summary ──
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Text(
                        text = "💰",
                        style = TextStyle(color = ColorProvider(Color.White))
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = "₹$todayExpenses spent today",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF00E676)),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(14.dp))

                // ── Quick Action ──
                Button(
                    text = "✨ Quick Add",
                    onClick = androidx.glance.action.actionStartActivity(
                        android.content.Intent(context, com.personal.lifeos.MainActivity::class.java).apply {
                            putExtra("OPEN_TAB", "money")
                        }
                    ),
                    modifier = GlanceModifier.fillMaxWidth()
                )
            }
        }
    }
}
