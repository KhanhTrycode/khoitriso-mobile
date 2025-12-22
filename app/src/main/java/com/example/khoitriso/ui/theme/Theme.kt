package com.example.khoitriso.ui.theme

import android.app.Activity
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
    primary = EdPrimary,
    onPrimary = EdWhite,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = EdPrimaryContainer,

    secondary = EdSecondary,
    onSecondary = EdWhite,
    secondaryContainer = Color(0xFF581C87),
    onSecondaryContainer = EdSecondaryContainer,

    tertiary = EdSuccess,
    onTertiary = EdWhite,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = EdSuccessContainer,

    background = EdGray900,
    onBackground = EdGray100,

    surface = EdGray800,
    onSurface = EdGray100,
    surfaceVariant = EdGray700,
    onSurfaceVariant = EdGray300,

    outline = EdGray600,
    outlineVariant = EdGray700,

    error = EdDanger,
    onError = EdWhite,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = EdErrorContainer,

    inverseSurface = EdGray100,
    inverseOnSurface = EdGray900,
    inversePrimary = EdPrimary,

    scrim = Color(0x99000000)
)

private val LightColorScheme = lightColorScheme(
    // Primary Colors - Blue (#3B82F6)
    primary = EdPrimary,
    onPrimary = EdWhite,
    primaryContainer = EdPrimaryContainer,
    onPrimaryContainer = Color(0xFF082F49),

    // Secondary Colors - Purple (#8B5CF6)
    secondary = EdSecondary,
    onSecondary = EdWhite,
    secondaryContainer = EdSecondaryContainer,
    onSecondaryContainer = Color(0xFF2E1065),

    // Tertiary Colors - Green (#10B981)
    tertiary = EdSuccess,
    onTertiary = EdWhite,
    tertiaryContainer = EdSuccessContainer,
    onTertiaryContainer = Color(0xFF064E3B),

    // Background - Light Gray (#F8FAFC)
    background = EdBackground,
    onBackground = EdGray800,

    // Surface - White
    surface = EdSurface,
    onSurface = EdGray800,
    surfaceVariant = EdGray100,
    onSurfaceVariant = EdGray500,

    // Outline
    outline = EdGray200,
    outlineVariant = EdGray300,

    // Error - Red (#EF4444)
    error = EdDanger,
    onError = EdWhite,
    errorContainer = EdErrorContainer,
    onErrorContainer = Color(0xFF7F1D1D),

    // Inverse
    inverseSurface = EdGray800,
    inverseOnSurface = EdGray100,
    inversePrimary = Color(0xFF93C5FD),

    scrim = Color(0x66000000)
)


@Composable
fun KhoiTriSoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = MyTypography,
        content = content
    )
}