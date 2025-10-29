package com.example.aplicacion.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val GrisOscuro: Color = TODO()

// --- Define el esquema de colores para el TEMA OSCURO ---
private val DarkColorScheme = darkColorScheme(
    primary = CafeClaro,         // Color principal en modo oscuro (un café más claro)
    onPrimary = Negro,           // Texto sobre primary (negro)
    primaryContainer = CafePrincipal, // Contenedores principales (café fuerte)
    onPrimaryContainer = Blanco, // Texto sobre primaryContainer (blanco)

    secondary = GrisMedio,       // Elementos secundarios (gris medio)
    onSecondary = Blanco,        // Texto sobre secondary (blanco)
    secondaryContainer = GrisOscuro, // Contenedores secundarios (gris oscuro)
    onSecondaryContainer = Blanco, // Texto sobre secondaryContainer (blanco)

    background = Negro,          // Fondo de la app en modo oscuro (negro)
    onBackground = Blanco,       // Texto sobre el fondo (blanco)

    surface = Negro,             // Superficies como Cards (negro)
    onSurface = Blanco,          // Texto sobre superficies (blanco)

    error = RojoError,           // Color para errores
    onError = Blanco             // Texto sobre errores
)

// --- Define el esquema de colores para el TEMA CLARO ---
private val LightColorScheme = lightColorScheme(
    primary = CafePrincipal,     // Color principal en modo claro (café fuerte)
    onPrimary = Blanco,          // Texto sobre primary (blanco)
    primaryContainer = CafeClaro, // Contenedores principales (café más claro)
    onPrimaryContainer = Negro,  // Texto sobre primaryContainer (negro)

    secondary = CafeClaro,       // Elementos secundarios (café más claro)
    onSecondary = Negro,         // Texto sobre secondary (negro)
    secondaryContainer = CafeMuyClaro, // Contenedores secundarios (café casi beige)
    onSecondaryContainer = Negro, // Texto sobre secondaryContainer (negro)

    background = Blanco,         // Fondo de la app en modo claro (blanco)
    onBackground = Negro,        // Texto sobre el fondo (negro)

    surface = GrisClaro,         // Superficies como Cards (gris claro)
    onSurface = Negro,           // Texto sobre superficies (negro)

    error = RojoError,           // Color para errores
    onError = Blanco             // Texto sobre errores
)

@Composable
fun CoffeeFlowerDefinitivoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true, // Esto usa los colores del fondo de pantalla en Android 12+
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
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asumimos que tienes un archivo Typography.kt
        content = content
    )
}