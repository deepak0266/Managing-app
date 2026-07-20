package com.personal.lifeos.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*

/**
 * Central motion system for Life OS.
 * All durations are capped at 300ms for snappy, premium feel.
 */
object MotionConstants {

    // ── Durations (never exceed 300ms) ──
    const val DURATION_FAST   = 150
    const val DURATION_MEDIUM = 200
    const val DURATION_NORMAL = 250
    const val DURATION_SLOW   = 300

    // ── Spring Specs ──
    val SpringFast: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

    val SpringMedium: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val SpringGentle: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )
}

// ═══════════════════════════════════════
//  Screen Enter/Exit Transitions
// ═══════════════════════════════════════

fun premiumEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(MotionConstants.DURATION_MEDIUM)) +
            slideInHorizontally(
                initialOffsetX = { it / 12 },
                animationSpec = tween(MotionConstants.DURATION_MEDIUM, easing = FastOutSlowInEasing)
            )
}

fun premiumExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST)) +
            slideOutHorizontally(
                targetOffsetX = { -it / 12 },
                animationSpec = tween(MotionConstants.DURATION_FAST, easing = FastOutSlowInEasing)
            )
}

fun premiumPopEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(MotionConstants.DURATION_MEDIUM)) +
            slideInHorizontally(
                initialOffsetX = { -it / 12 },
                animationSpec = tween(MotionConstants.DURATION_MEDIUM, easing = FastOutSlowInEasing)
            )
}

fun premiumPopExitTransition(): ExitTransition {
    return fadeOut(animationSpec = tween(MotionConstants.DURATION_FAST)) +
            slideOutHorizontally(
                targetOffsetX = { it / 12 },
                animationSpec = tween(MotionConstants.DURATION_FAST, easing = FastOutSlowInEasing)
            )
}
