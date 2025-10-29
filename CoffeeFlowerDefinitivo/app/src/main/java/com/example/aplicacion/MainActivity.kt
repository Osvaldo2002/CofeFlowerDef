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

// --- IMPORTACIONES CLAVE ---
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.ui.theme.CoffeeFlowerDefinitivoTheme // <-- 1. IMPORTA TU TEMA

// ---

// Importamos TODAS las pantallas
import com.example.aplicacion.screens.InicioScreen
import com.example.aplicacion.screens.LoginSCreen
import com.example.aplicacion.screens.ProductosScreen
import com.example.aplicacion.screens.AgregarProductoScreen
import com.example.aplicacion.screens.CarritoScreen
import com.example.aplicacion.screens.QuienesSomosScreen
// Importamos las RUTAS
import com.example.aplicacion.AppScreens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // --- 2. ENVUELVE TU APP CON EL TEMA ---
            CoffeeFlowerDefinitivoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // <-- Ahora usará el color de tu tema
                ) {
                    AppNavigation()
                }
            }
            // ---
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // --- CREAMOS UNA ÚNICA INSTANCIA DEL VIEWMODEL AQUÍ ---
    val carritoViewModel: CarritoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.INICIO // Pantalla inicial
    ) {
        // Pantallas que no necesitan el ViewModel
        composable(route = AppScreens.INICIO) {
            InicioScreen(navController = navController)
        }
        composable(route = AppScreens.LOGIN) {
            LoginSCreen(navController = navController)
        }
        composable(route = AppScreens.QUIENES_SOMOS) {
            QuienesSomosScreen(navController = navController)
        }

        // --- PANTALLAS QUE SÍ NECESITAN EL VIEWMODEL ---

        // 1. Pantalla de Productos
        composable(route = AppScreens.PRODUCTOS) {
            ProductosScreen(
                navController = navController,
                viewModel = carritoViewModel
            )
        }

        // 2. Pantalla de Agregar Producto
        composable(route = AppScreens.AGREGAR_PRODUCTO) {
            AgregarProductoScreen(
                navController = navController,
                viewModel = carritoViewModel
            )
        }

        // 3. Pantalla de Carrito
        composable(route = AppScreens.CARRITO) {
            CarritoScreen(
                navController = navController,
                viewModel = carritoViewModel
            )
        }
    }
}