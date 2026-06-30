package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme =
  lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimaryPurple,
    primaryContainer = PurpleContainer1,
    onPrimaryContainer = OnPurpleContainer,
    secondary = PurpleContainer2,
    onSecondary = OnPurpleContainer,
    secondaryContainer = PurpleContainer2,
    onSecondaryContainer = OnPurpleContainer,
    tertiary = PurpleContainer3,
    onTertiary = OnPurpleContainer,
    background = BgColor,
    surface = SurfaceColor,
    onBackground = TextPrimary,
    onSurface = TextSecondary,
    outline = BorderColor,
    outlineVariant = BorderColor
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = ColorScheme, typography = Typography, content = content)
}
