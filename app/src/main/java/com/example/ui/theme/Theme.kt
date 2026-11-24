package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SeniorIvoryColorScheme =
  lightColorScheme(
    primary = HerbGreen,
    onPrimary = Color.White,
    primaryContainer = HerbGreenSoft,
    onPrimaryContainer = HerbGreenDark,
    secondary = WaterBlue,
    onSecondary = Color.White,
    secondaryContainer = WaterCardBg,
    onSecondaryContainer = WaterBlueDark,
    background = IvoryBackground,
    onBackground = InkPrimary,
    surface = IvorySurface,
    onSurface = InkPrimary,
    surfaceVariant = IvoryHeaderSurface,
    onSurfaceVariant = InkSecondary,
    outline = IvoryBorder,
  )

@Composable
fun SeniorHealthTheme(content: @Composable () -> Unit) {
  // Always use the eye-friendly warm ivory high-contrast light palette designed for seniors.
  MaterialTheme(
    colorScheme = SeniorIvoryColorScheme,
    typography = SeniorTypography,
    content = content,
  )
}
