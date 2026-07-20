package com.personal.lifeos.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.personal.lifeos.ui.theme.LocalPremiumColors

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val premiumColors = LocalPremiumColors.current
    val shimmerColors = listOf(
        premiumColors.shimmerBase,
        premiumColors.shimmerHighlight,
        premiumColors.shimmerBase
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(modifier = modifier.background(brush))
}

@Composable
fun ShimmerCardLoading(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp
) {
    ShimmerEffect(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
    )
}

@Composable
fun ShimmerListLoading(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            ShimmerCardLoading()
        }
    }
}

@Composable
fun ShimmerChartLoading(
    modifier: Modifier = Modifier
) {
    ShimmerEffect(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(24.dp))
    )
}
