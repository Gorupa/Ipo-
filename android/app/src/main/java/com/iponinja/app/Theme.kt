package com.iponinja.app

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// Brand palette pulled from the app icon: deep navy, ninja blue, gain green.
private val NinjaGreen = Color(0xFF34D9A0)
private val NinjaGreenDark = Color(0xFF0EA371)
private val NinjaBlue = Color(0xFF4C8DFF)
private val NinjaBlueDark = Color(0xFF2F6FE0)
private val Navy900 = Color(0xFF02132E)
private val Navy800 = Color(0xFF0B2544)
private val Navy700 = Color(0xFF15325A)
private val ErrorRed = Color(0xFFFF6B6B)

private val DarkColors = darkColorScheme(
    primary = NinjaGreen,
    onPrimary = Color(0xFF00281B),
    secondary = NinjaBlue,
    onSecondary = Color.White,
    background = Navy900,
    onBackground = Color(0xFFE6EDF7),
    surface = Navy800,
    onSurface = Color(0xFFE6EDF7),
    surfaceVariant = Navy700,
    onSurfaceVariant = Color(0xFFB7C4D9),
    outline = Color(0xFF2C4A72),
    error = ErrorRed,
    onError = Color(0xFF3A0000)
)

private val LightColors = lightColorScheme(
    primary = NinjaGreenDark,
    onPrimary = Color.White,
    secondary = NinjaBlueDark,
    onSecondary = Color.White,
    background = Color(0xFFF5F8FC),
    onBackground = Color(0xFF0B1B2E),
    surface = Color.White,
    onSurface = Color(0xFF0B1B2E),
    surfaceVariant = Color(0xFFE7EEF7),
    onSurfaceVariant = Color(0xFF44546A),
    outline = Color(0xFFD3DEEC),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

val AppTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp)
)

@Composable
fun IpoNinjaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

// Status accent colors used across cards/badges regardless of theme.
object StatusColors {
    val Open = NinjaGreen
    val Upcoming = NinjaBlue
    val ClosingSoon = Color(0xFFFFB020)
    val Closed = Color(0xFF8A97AB)
}
