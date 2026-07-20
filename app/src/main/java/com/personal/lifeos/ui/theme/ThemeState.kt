package com.personal.lifeos.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Supported theme modes: Light, Dark, AMOLED Black
 */
enum class ThemeMode { Light, Dark, AmoledBlack }

/**
 * Supported accent color palettes
 */
enum class AccentColor { Purple, Emerald, Cyan, Orange, Blue }

/**
 * Extended color properties not covered by Material3 ColorScheme.
 * Accessed via LocalPremiumColors.current throughout the app.
 */
@Immutable
data class PremiumColorExtensions(
    val accentGradient: Brush = Brush.linearGradient(listOf(Color(0xFF7C4DFF), Color(0xFFB388FF))),
    val cardGlow: Color = Color(0xFF7C4DFF).copy(alpha = 0.08f),
    val shimmerBase: Color = Color(0xFF1C1C2E),
    val shimmerHighlight: Color = Color(0xFF2A2A40),
    val successColor: Color = Color(0xFF00E676),
    val warningColor: Color = Color(0xFFFFAB00),
    val errorColor: Color = Color(0xFFFF5252),
    val infoColor: Color = Color(0xFF448AFF),
    val glassOverlay: Color = Color(0x1AFFFFFF),
    val glassBorder: Color = Color(0x1AFFFFFF),
    val streakFlame: Color = Color(0xFFFF6D00),
    val streakGlow: Color = Color(0xFFFFAB40)
)

val LocalPremiumColors = compositionLocalOf { PremiumColorExtensions() }
