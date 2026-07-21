package com.personal.lifeos.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun PremiumBottomNav(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("reminders", "Tasks", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
        NavItem("money", "Money", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
        NavItem("memory", "Memory", Icons.Filled.Psychology, Icons.Outlined.Psychology),
        NavItem("habits", "Habits", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
        NavItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route

                PremiumNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun PremiumNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f,
        animationSpec = tween(200),
        label = "icon_alpha"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val columnScope = this
        Box(contentAlignment = Alignment.Center) {
            // Selection indicator pill
            columnScope.AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(200)) + scaleIn(
                    initialScale = 0.6f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ),
                exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.6f)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }

            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                modifier = Modifier.size((24 * animatedScale).dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = animatedAlpha)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(100)) + shrinkVertically()
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
            )
        }
    }
}
