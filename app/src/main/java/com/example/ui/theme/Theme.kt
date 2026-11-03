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
    primary = AgriGreenDarkPrimary,
    onPrimary = Color(0xFF00390E),
    primaryContainer = AgriGreenDarkContainer,
    onPrimaryContainer = Color(0xFFA5F4AC),
    secondary = HarvestGoldDark,
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF693C00),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = Color(0xFF80CBC4),
    background = AgriBackgroundDark,
    onBackground = AgriOnSurfaceDark,
    surface = AgriSurfaceDark,
    onSurface = AgriOnSurfaceDark,
    surfaceVariant = AgriSurfaceVariantDark,
    outline = Color(0xFF8A9588)
)

private val LightColorScheme = lightColorScheme(
    primary = AgriGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = AgriGreenContainer,
    onPrimaryContainer = AgriOnGreenContainer,
    secondary = HarvestGold,
    onSecondary = Color.White,
    secondaryContainer = HarvestGoldContainer,
    onSecondaryContainer = HarvestOnGoldContainer,
    tertiary = EarthBrown,
    background = AgriBackgroundLight,
    onBackground = AgriOnSurfaceLight,
    surface = AgriSurfaceLight,
    onSurface = AgriOnSurfaceLight,
    surfaceVariant = AgriSurfaceVariantLight,
    outline = AgriOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Enable Material 3 Dynamic Colors on Android 12+
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
