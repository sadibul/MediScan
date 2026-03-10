package com.mediscan.app.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * MediScan Material 3 Theme.
 * Light color scheme uses MediScan's brand colors (MediBlue primary).
 * Supports dark theme and dynamic colors on Android 12+.
 */

private val LightColorScheme = lightColorScheme(
    primary = MediBlue,
    onPrimary = White,
    primaryContainer = MediBlueContainer,
    onPrimaryContainer = MediBlueDark,
    secondary = HealthGreen,
    onSecondary = White,
    secondaryContainer = HealthGreenLight,
    onSecondaryContainer = HealthGreenDark,
    tertiary = WarningOrange,
    onTertiary = White,
    tertiaryContainer = WarningOrangeLight,
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    background = BackgroundGray,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
)

private val DarkColorScheme = darkColorScheme(
    primary = MediBlueLight,
    onPrimary = MediBlueDark,
    primaryContainer = MediBlueDark,
    onPrimaryContainer = MediBlueLight,
    secondary = HealthGreenLight,
    onSecondary = HealthGreenDark,
    secondaryContainer = HealthGreenDark,
    onSecondaryContainer = HealthGreenLight,
    tertiary = WarningOrangeLight,
    onTertiary = WarningOrange,
    error = ErrorRedLight,
    onError = ErrorRed,
    errorContainer = ErrorRed,
)

@Composable
fun MediScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MediScanTypography,
        content = content
    )
}
