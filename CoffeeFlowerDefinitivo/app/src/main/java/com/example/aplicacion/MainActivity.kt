package com.example.aplicacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion.ui.theme.CoffeeFlowerDefinitivoTheme
import com.example.aplicacion.screens.* // Importa TODAS las pantallas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeFlowerDefinitivoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val carritoViewModel: CarritoViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.INICIO
    ) {

        // --- PANTALLAS PRINCIPALES DE USUARIO ---

        composable(route = AppScreens.INICIO) {
            InicioScreen(
                navController = navController,
                authViewModel = authViewModel,
                carritoViewModel = carritoViewModel
            )
        }

        composable(route = AppScreens.PRODUCTOS) {
            ProductosScreen(
                navController = navController,
                viewModel = carritoViewModel,
                authViewModel = authViewModel
            )
        }

        composable(route = AppScreens.CARRITO) {
            CarritoScreen(
                navController = navController,
                viewModel = carritoViewModel,
                authViewModel = authViewModel
            )
        }

        composable(route = AppScreens.QUIENES_SOMOS) {
            QuienesSomosScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // --- FLUJO DE LOGIN Y PAGO ---

        composable(route = AppScreens.LOGIN) {
            LoginScreen( // <-- NOMBRE CORREGIDO
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(
            route = AppScreens.BOLETA_GENERADA + "/{boletaId}",
            arguments = listOf(navArgument("boletaId") { type = NavType.StringType })
        ) { backStackEntry ->
            BoletaGeneradaScreen(
                navController = navController,
                boletaId = backStackEntry.arguments?.getString("boletaId")
            )
        }

        // --- === PANTALLAS DE ADMIN === ---

        composable(route = AppScreens.ADMIN_PANEL) {
            AdminPanelScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(route = AppScreens.AGREGAR_PRODUCTO) {
            AgregarProductoScreen(
                navController = navController,
                viewModel = carritoViewModel
            )
        }

        composable(
            route = AppScreens.EDITAR_PRODUCTO + "/{productoId}",
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            EditarProductoScreen(
                navController = navController,
                viewModel = carritoViewModel,
                productoId = backStackEntry.arguments?.getInt("productoId")
            )
        }

        composable(route = AppScreens.ADMIN_CATEGORIAS) {
            AdminCategoriasScreen(navController = navController)
        }

        composable(route = AppScreens.ADMIN_BOLETAS) {
            AdminBoletasScreen(
                navController = navController,
                viewModel = carritoViewModel
            )
        }
    }
}