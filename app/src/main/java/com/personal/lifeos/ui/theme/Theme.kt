package com.personal.lifeos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════
//  Color Scheme Factories
// ═══════════════════════════════════════════════════

private fun premiumDarkColorScheme(accent: AccentColor) = darkColorScheme(
    primary = accent.primary(),
    onPrimary = Color.White,
    primaryContainer = accent.primaryContainer(),
    onPrimaryContainer = accent.primary(),
    secondary = accent.secondary(),
    onSecondary = Color.Black,
    secondaryContainer = accent.secondaryContainer(),
    onSecondaryContainer = accent.secondary(),
    tertiary = accent.tertiary(),
    background = DarkBackground,
    onBackground = OnDarkBackground,
    surface = DarkSurface,
    onSurface = OnDarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkSurfaceVariant,
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF3A3850),
    outlineVariant = Color(0xFF2A2840)
)

private fun premiumAmoledColorScheme(accent: AccentColor) = darkColorScheme(
    primary = accent.primary(),
    onPrimary = Color.White,
    primaryContainer = accent.primaryContainer(),
    onPrimaryContainer = accent.primary(),
    secondary = accent.secondary(),
    onSecondary = Color.Black,
    secondaryContainer = accent.secondaryContainer(),
    onSecondaryContainer = accent.secondary(),
    tertiary = accent.tertiary(),
    background = AmoledBackground,
    onBackground = OnDarkBackground,
    surface = AmoledSurface,
    onSurface = OnDarkSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = OnDarkSurfaceVariant,
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF2A2840),
    outlineVariant = Color(0xFF1A1830)
)

private fun premiumLightColorScheme(accent: AccentColor) = lightColorScheme(
    primary = accent.primary(),
    onPrimary = Color.White,
    primaryContainer = accent.lightPrimaryContainer(),
    onPrimaryContainer = accent.primary(),
    secondary = accent.secondary(),
    onSecondary = Color.White,
    secondaryContainer = accent.lightSecondaryContainer(),
    onSecondaryContainer = accent.secondary(),
    tertiary = accent.tertiary(),
    background = LightBackground,
    onBackground = OnLightBackground,
    surface = LightSurface,
    onSurface = OnLightSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = OnLightSurfaceVariant,
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFFCAC4D0),
    outlineVariant = Color(0xFFE7E0EC)
)

// ═══════════════════════════════════════════════════
//  Accent Color Extension Functions
// ═══════════════════════════════════════════════════

internal fun AccentColor.primary(): Color = when (this) {
    AccentColor.Purple -> PurplePrimary
    AccentColor.Emerald -> EmeraldPrimary
    AccentColor.Cyan -> CyanPrimary
    AccentColor.Orange -> OrangePrimary
    AccentColor.Blue -> BluePrimary
}

private fun AccentColor.primaryContainer(): Color = when (this) {
    AccentColor.Purple -> PurplePrimaryContainer
    AccentColor.Emerald -> EmeraldPrimaryContainer
    AccentColor.Cyan -> CyanPrimaryContainer
    AccentColor.Orange -> OrangePrimaryContainer
    AccentColor.Blue -> BluePrimaryContainer
}

internal fun AccentColor.secondary(): Color = when (this) {
    AccentColor.Purple -> PurpleSecondary
    AccentColor.Emerald -> EmeraldSecondary
    AccentColor.Cyan -> CyanSecondary
    AccentColor.Orange -> OrangeSecondary
    AccentColor.Blue -> BlueSecondary
}

private fun AccentColor.secondaryContainer(): Color = when (this) {
    AccentColor.Purple -> PurpleSecondaryContainer
    AccentColor.Emerald -> EmeraldSecondaryContainer
    AccentColor.Cyan -> CyanSecondaryContainer
    AccentColor.Orange -> OrangeSecondaryContainer
    AccentColor.Blue -> BlueSecondaryContainer
}

private fun AccentColor.tertiary(): Color = when (this) {
    AccentColor.Purple -> PurpleTertiary
    AccentColor.Emerald -> EmeraldTertiary
    AccentColor.Cyan -> CyanTertiary
    AccentColor.Orange -> OrangeTertiary
    AccentColor.Blue -> BlueTertiary
}

private fun AccentColor.lightPrimaryContainer(): Color = when (this) {
    AccentColor.Purple -> PurpleLightPrimaryContainer
    AccentColor.Emerald -> EmeraldLightPrimaryContainer
    AccentColor.Cyan -> CyanLightPrimaryContainer
    AccentColor.Orange -> OrangeLightPrimaryContainer
    AccentColor.Blue -> BlueLightPrimaryContainer
}

private fun AccentColor.lightSecondaryContainer(): Color = when (this) {
    AccentColor.Purple -> PurpleLightSecondaryContainer
    AccentColor.Emerald -> EmeraldLightSecondaryContainer
    AccentColor.Cyan -> CyanLightSecondaryContainer
    AccentColor.Orange -> OrangeLightSecondaryContainer
    AccentColor.Blue -> BlueLightSecondaryContainer
}

// ═══════════════════════════════════════════════════
//  Main Theme Composable
// ═══════════════════════════════════════════════════

@Composable
fun LifeOSTheme(
    themeMode: ThemeMode = ThemeMode.Dark,
    accentColor: AccentColor = AccentColor.Purple,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.Light -> premiumLightColorScheme(accentColor)
        ThemeMode.Dark -> premiumDarkColorScheme(accentColor)
        ThemeMode.AmoledBlack -> premiumAmoledColorScheme(accentColor)
    }

    val premiumColors = PremiumColorExtensions(
        accentGradient = Brush.linearGradient(
            listOf(accentColor.primary(), accentColor.secondary())
        ),
        cardGlow = accentColor.primary().copy(alpha = 0.08f),
        shimmerBase = if (themeMode == ThemeMode.Light) Color(0xFFE8E6F0) else Color(0xFF1C1C2E),
        shimmerHighlight = if (themeMode == ThemeMode.Light) Color(0xFFF5F3FA) else Color(0xFF2A2A40),
        successColor = SuccessGreen,
        warningColor = WarningAmber,
        errorColor = ErrorRed,
        infoColor = InfoBlue,
        glassOverlay = if (themeMode == ThemeMode.Light) GlassDark else GlassWhite,
        glassBorder = if (themeMode == ThemeMode.Light) Color(0x15000000) else GlassBorder,
        streakFlame = StreakFlame,
        streakGlow = StreakGlow
    )

    CompositionLocalProvider(
        LocalPremiumColors provides premiumColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PremiumTypography,
            content = content
        )
    }
}
