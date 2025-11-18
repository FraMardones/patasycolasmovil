package com.example.patas_y_colas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores para el tema claro, usando tus nuevos colores
private val LightColorScheme = lightColorScheme(
    primary = PetSageGreen,
    secondary = PetSand,
    tertiary = PetOchre,
    background = PetBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = PetTextDark,
    onTertiary = PetTextDark,
    onBackground = PetTextDark,
    onSurface = PetTextDark,
)

// Paleta de colores para un futuro tema oscuro (puedes personalizarla más adelante)
private val DarkColorScheme = darkColorScheme(
    primary = PetSageGreen,
    secondary = PetSand,
    tertiary = PetOchre,
    background = PetTextDark,
    surface = Color(0xFF3C3C3C),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun Patas_y_ColasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Desactivado para forzar nuestra paleta personalizada
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Usamos la paleta clara por defecto
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Hacemos la barra de estado transparente para que se vea el color de la cabecera
            window.statusBarColor = Color.Transparent.toArgb()
            // Le decimos al sistema que los iconos de la barra de estado deben ser oscuros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

