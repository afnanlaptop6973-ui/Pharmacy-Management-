package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PharmacyPrimaryDark,
    onPrimary = PharmacyOnPrimaryDark,
    primaryContainer = PharmacyPrimaryContainerDark,
    onPrimaryContainer = PharmacyOnPrimaryContainerDark,
    secondary = PharmacySecondaryDark,
    onSecondary = PharmacyOnSecondaryDark,
    secondaryContainer = PharmacySecondaryContainerDark,
    onSecondaryContainer = PharmacyOnSecondaryContainerDark,
    tertiary = PharmacyTertiaryDark,
    onTertiary = PharmacyOnTertiaryDark,
    tertiaryContainer = PharmacyTertiaryContainerDark,
    onTertiaryContainer = PharmacyOnTertiaryContainerDark,
    background = PharmacyBackgroundDark,
    onBackground = PharmacyOnBackgroundDark,
    surface = PharmacySurfaceDark,
    onSurface = PharmacyOnSurfaceDark,
    surfaceVariant = PharmacySurfaceVariantDark,
    onSurfaceVariant = PharmacyOnSurfaceVariantDark,
    outline = PharmacyOutlineDark,
    outlineVariant = Color(0xFF1E453C),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

private val LightColorScheme = lightColorScheme(
    primary = PharmacyPrimary,
    onPrimary = PharmacyOnPrimary,
    primaryContainer = PharmacyPrimaryContainer,
    onPrimaryContainer = PharmacyOnPrimaryContainer,
    secondary = PharmacySecondary,
    onSecondary = PharmacyOnSecondary,
    secondaryContainer = PharmacySecondaryContainer,
    onSecondaryContainer = PharmacyOnSecondaryContainer,
    tertiary = PharmacyTertiary,
    onTertiary = PharmacyOnTertiary,
    tertiaryContainer = PharmacyTertiaryContainer,
    onTertiaryContainer = PharmacyOnTertiaryContainer,
    background = PharmacyBackgroundLight,
    onBackground = PharmacyOnBackgroundLight,
    surface = PharmacySurfaceLight,
    onSurface = PharmacyOnSurfaceLight,
    surfaceVariant = PharmacySurfaceVariantLight,
    onSurfaceVariant = PharmacyOnSurfaceVariantLight,
    outline = PharmacyOutlineLight,
    outlineVariant = Color(0xFFD1FAE5),
    error = StatusCritical,
    onError = Color.White,
    errorContainer = StatusCriticalBg,
    onErrorContainer = Color(0xFF991B1B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false, // Keep bespoke medical shop branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
