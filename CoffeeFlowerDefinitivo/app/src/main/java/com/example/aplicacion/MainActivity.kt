package com.example.aplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// No importamos AplicacionTheme porque no existe

// Importamos TODAS las pantallas
import com.example.aplicacion.screens.InicioScreen
import com.example.aplicacion.screens.LoginSCreen
import com.example.aplicacion.screens.ProductosScreen
import com.example.aplicacion.screens.AgregarProductoScreen
import com.example.aplicacion.screens.CarritoScreen
import com.example.aplicacion.screens.QuienesSomosScreen
// Importamos las RUTAS
import com.example.aplicacion.AppScreens
import com.example.aplicacion.screens.LoginSCreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // HEMOS QUITADO EL "AplicacionTheme" QUE DABA ERROR
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // Llamamos directamente a nuestra navegación
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.INICIO // Pantalla inicial
    ) {
        // Definimos todas las rutas
        composable(route = AppScreens.INICIO) {
            InicioScreen(navController = navController)
        }
        composable(route = AppScreens.LOGIN) {
            LoginSCreen(navController = navController)
        }
        composable(route = AppScreens.PRODUCTOS) {
            ProductosScreen(navController = navController)
        }
        composable(route = AppScreens.AGREGAR_PRODUCTO) {
            AgregarProductoScreen(navController = navController)
        }
        composable(route = AppScreens.CARRITO) {
            CarritoScreen(navController = navController)
        }
        composable(route = AppScreens.QUIENES_SOMOS) {
            QuienesSomosScreen(navController = navController)
        }
    }
}