package com.personal.lifeos.sync

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.personal.lifeos.ui.components.PremiumTopBar
import com.personal.lifeos.ui.theme.*
import java.util.concurrent.TimeUnit

@Composable
fun SettingsScreen(
    themeMode: ThemeMode = ThemeMode.Dark,
    accentColor: AccentColor = AccentColor.Purple,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onAccentColorChange: (AccentColor) -> Unit = {}
) {
    var isCloudSyncEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Premium Top Bar ──
        PremiumTopBar(title = "Settings")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ═══════════════════════════════════
            //  APPEARANCE SECTION
            // ═══════════════════════════════════
            SectionLabel("Appearance")

            // ── Theme Mode ──
            Text(
                "Theme",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ThemeModeSelector(
                currentMode = themeMode,
                onModeSelected = onThemeModeChange
            )

            // ── Accent Color ──
            Text(
                "Accent Color",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            AccentColorSelector(
                currentAccent = accentColor,
                onAccentSelected = onAccentColorChange
            )

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // ═══════════════════════════════════
            //  DATA SECTION
            // ═══════════════════════════════════
            SectionLabel("Data & Backup")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Cloud Sync",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Securely backup your data to Supabase every 12 hours.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Switch(
                        checked = isCloudSyncEnabled,
                        onCheckedChange = {
                            isCloudSyncEnabled = it
                            if (it) {
                                // Enable periodic sync worker
                                val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(12, TimeUnit.HOURS).build()
                                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                                    "supabase_sync",
                                    ExistingPeriodicWorkPolicy.KEEP,
                                    syncRequest
                                )
                            } else {
                                // Cancel sync worker
                                WorkManager.getInstance(context).cancelUniqueWork("supabase_sync")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // ═══════════════════════════════════
            //  ABOUT SECTION
            // ═══════════════════════════════════
            SectionLabel("About")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Version",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Made with",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "❤\uFE0F & Kotlin",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ThemeModeSelector(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ThemeMode.values().forEach { mode ->
            val isSelected = mode == currentMode

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.95f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "theme_scale"
            )

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onModeSelected(mode) },
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    MaterialTheme.colorScheme.primary
                ) else null
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (mode) {
                            ThemeMode.Light -> "☀\uFE0F"
                            ThemeMode.Dark -> "🌙"
                            ThemeMode.AmoledBlack -> "⬛"
                        },
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (mode) {
                            ThemeMode.Light -> "Light"
                            ThemeMode.Dark -> "Dark"
                            ThemeMode.AmoledBlack -> "AMOLED"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AccentColorSelector(
    currentAccent: AccentColor,
    onAccentSelected: (AccentColor) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AccentColor.values().forEach { accent ->
            val isSelected = accent == currentAccent
            val color = accent.primary()

            val size by animateFloatAsState(
                targetValue = if (isSelected) 48f else 38f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "color_size"
            )

            Box(
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onAccentSelected(accent) }
                    .then(
                        if (isSelected) Modifier.border(
                            3.dp,
                            Color.White.copy(alpha = 0.6f),
                            CircleShape
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
