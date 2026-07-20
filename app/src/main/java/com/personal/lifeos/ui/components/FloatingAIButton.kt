package com.personal.lifeos.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FloatingAIButton(
    onVoiceClick: () -> Unit,
    onTextClick: () -> Unit,
    onCameraClick: () -> Unit,
    onQuickExpenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Pulsating idle animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // ── Expanded Menu ──
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(150)) + scaleIn(
                initialScale = 0.3f,
                transformOrigin = TransformOrigin(1f, 1f),
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ),
            exit = fadeOut(tween(100)) + scaleOut(
                targetScale = 0.3f,
                transformOrigin = TransformOrigin(1f, 1f)
            )
        ) {
            Surface(
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AIMenuItem(
                        icon = Icons.Filled.Mic,
                        label = "Voice Input",
                        onClick = { onVoiceClick(); isExpanded = false }
                    )
                    AIMenuItem(
                        icon = Icons.Filled.Edit,
                        label = "Text Input",
                        onClick = { onTextClick(); isExpanded = false }
                    )
                    AIMenuItem(
                        icon = Icons.Filled.CameraAlt,
                        label = "Scan Receipt",
                        onClick = { onCameraClick(); isExpanded = false }
                    )
                    AIMenuItem(
                        icon = Icons.Filled.Payment,
                        label = "Quick Expense",
                        onClick = { onQuickExpenseClick(); isExpanded = false }
                    )
                }
            }
        }

        // ── Main FAB ──
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .size(60.dp)
                .scale(if (!isExpanded) pulseScale else 1f)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.AutoAwesome,
                contentDescription = "AI Assistant",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun AIMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
