// ===============================
// File: app/src/main/java/com/maxli/coursegpa/ui/theme/Theme.kt
// ===============================
package com.maxli.coursegpa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    // Dark mode: navy base + orange accents + light blue highlights
    primary = RWNavy,
    onPrimary = Color.White,

    secondary = RWLightBlue,
    onSecondary = RWNavy,

    tertiary = RWOrange,
    onTertiary = RWNavy,

    background = Color(0xFF0B1220),
    onBackground = Color.White,
    surface = Color(0xFF0F1A30),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    // Light mode: light blue base, navy structure, orange accents
    primary = RWLightBlue,
    onPrimary = RWNavy,

    secondary = RWNavy,
    onSecondary = Color.White,

    tertiary = RWOrange,
    onTertiary = RWNavy,

    background = RWLightBlue,
    onBackground = RWNavy,
    surface = Color.White,
    onSurface = RWNavy
)

@Composable
fun CourseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
