package com.personal.lifeos.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════
//  PREMIUM COLOR SYSTEM — Life OS
//  Inspired by Notion · Linear · Pixel · Arc · Superhuman
// ═══════════════════════════════════════════════════════════

// ── Background Tiers (Dark) ──
val DarkBackground       = Color(0xFF0A0A0F)
val DarkSurface          = Color(0xFF141420)
val DarkSurfaceVariant   = Color(0xFF1C1C2E)
val DarkSurfaceElevated  = Color(0xFF242438)

// ── Background Tiers (AMOLED) ──
val AmoledBackground       = Color(0xFF000000)
val AmoledSurface          = Color(0xFF0A0A0F)
val AmoledSurfaceVariant   = Color(0xFF121220)

// ── Background Tiers (Light) ──
val LightBackground       = Color(0xFFF8F7FC)
val LightSurface          = Color(0xFFFFFFFF)
val LightSurfaceVariant   = Color(0xFFF0EEF6)

// ════════════════════════════════════
//  ACCENT PALETTES
// ════════════════════════════════════

// Deep Purple (Default)
val PurplePrimary            = Color(0xFF7C4DFF)
val PurplePrimaryContainer   = Color(0xFF1A1030)
val PurpleSecondary          = Color(0xFFB388FF)
val PurpleSecondaryContainer = Color(0xFF22153D)
val PurpleTertiary           = Color(0xFFCE93D8)

// Emerald
val EmeraldPrimary            = Color(0xFF00E676)
val EmeraldPrimaryContainer   = Color(0xFF0D2818)
val EmeraldSecondary          = Color(0xFF69F0AE)
val EmeraldSecondaryContainer = Color(0xFF0A3320)
val EmeraldTertiary           = Color(0xFF80CBC4)

// Cyan
val CyanPrimary            = Color(0xFF00E5FF)
val CyanPrimaryContainer   = Color(0xFF0D2A30)
val CyanSecondary          = Color(0xFF84FFFF)
val CyanSecondaryContainer = Color(0xFF0A2E33)
val CyanTertiary           = Color(0xFF80DEEA)

// Orange
val OrangePrimary            = Color(0xFFFF6D00)
val OrangePrimaryContainer   = Color(0xFF301500)
val OrangeSecondary          = Color(0xFFFFAB40)
val OrangeSecondaryContainer = Color(0xFF3D1E00)
val OrangeTertiary           = Color(0xFFFFCC80)

// Royal Blue
val BluePrimary            = Color(0xFF448AFF)
val BluePrimaryContainer   = Color(0xFF0D1A33)
val BlueSecondary          = Color(0xFF82B1FF)
val BlueSecondaryContainer = Color(0xFF0F2040)
val BlueTertiary           = Color(0xFF90CAF9)

// ── Light Mode Containers ──
val PurpleLightPrimaryContainer   = Color(0xFFE8DEFF)
val PurpleLightSecondaryContainer = Color(0xFFF0E8FF)
val EmeraldLightPrimaryContainer   = Color(0xFFD0FFE0)
val EmeraldLightSecondaryContainer = Color(0xFFE0FFE8)
val CyanLightPrimaryContainer      = Color(0xFFD0F8FF)
val CyanLightSecondaryContainer    = Color(0xFFE0FAFF)
val OrangeLightPrimaryContainer    = Color(0xFFFFE0CC)
val OrangeLightSecondaryContainer  = Color(0xFFFFF0E0)
val BlueLightPrimaryContainer      = Color(0xFFD8E6FF)
val BlueLightSecondaryContainer    = Color(0xFFE0EEFF)

// ════════════════════════════════════
//  SEMANTIC COLORS
// ════════════════════════════════════
val SuccessGreen = Color(0xFF00E676)
val WarningAmber = Color(0xFFFFAB00)
val ErrorRed     = Color(0xFFFF5252)
val InfoBlue     = Color(0xFF448AFF)

// ── On-Colors ──
val OnDarkBackground     = Color(0xFFE8E6F0)
val OnDarkSurface        = Color(0xFFE8E6F0)
val OnDarkSurfaceVariant = Color(0xFF9996A8)
val OnLightBackground     = Color(0xFF1C1B1F)
val OnLightSurface        = Color(0xFF1C1B1F)
val OnLightSurfaceVariant = Color(0xFF49454F)

// ── Glass / Overlay ──
val GlassWhite  = Color(0x1AFFFFFF)
val GlassDark   = Color(0x33000000)
val GlassBorder = Color(0x1AFFFFFF)

// ── Status Colors ──
val TaskPending   = Color(0xFFFFAB00)
val TaskOverdue   = Color(0xFFFF5252)
val TaskCompleted = Color(0xFF00E676)

// ── Streak Colors ──
val StreakFlame = Color(0xFFFF6D00)
val StreakGlow  = Color(0xFFFFAB40)

// ── Category Emoji Map (for expense cards) ──
val CategoryColors = mapOf(
    "food" to Color(0xFFFF8A65),
    "travel" to Color(0xFF4FC3F7),
    "shopping" to Color(0xFFBA68C8),
    "subscription" to Color(0xFF7986CB),
    "health" to Color(0xFF81C784),
    "entertainment" to Color(0xFFFFD54F),
    "bills" to Color(0xFFE57373),
    "salary" to Color(0xFF4DB6AC),
    "other" to Color(0xFF90A4AE)
)
