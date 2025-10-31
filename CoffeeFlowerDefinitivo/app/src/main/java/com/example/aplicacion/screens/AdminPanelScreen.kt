package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp // Icono para "Cerrar Sesión"
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens
import com.example.aplicacion.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador 👑") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer, // Color que destaca el modo Admin
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                actions = {
                    // Botón de Cerrar Sesión movido a la barra superior
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(AppScreens.INICIO) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Simplificamos la barra inferior para solo el botón "Ver como Cliente"
            BottomAppBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { navController.navigate(AppScreens.INICIO) { popUpTo(0) } }) {
                        Text("Volver al Modo Cliente")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp) // Aumento de padding para mejor separación
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- OPCIONES DE GESTIÓN DE PRODUCTOS ---

            // 1. Agregar Nuevo Producto (Nueva Opción Importante)
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(AppScreens.AGREGAR_PRODUCTO) },
                modifier = Modifier.fillMaxWidth(),
                icon = { Icon(Icons.Default.Add, contentDescription = "Añadir") },
                text = { Text("Añadir Nuevo Producto") }
            )

            Divider()

            // 2. Gestionar Productos
            Button(
                onClick = { navController.navigate(AppScreens.PRODUCTOS) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = "Productos", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Editar/Eliminar Productos")
            }

            // 3. Gestionar Categorías
            Button(
                onClick = { navController.navigate(AppScreens.ADMIN_CATEGORIAS) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = "Categorías", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Gestionar Categorías")
            }

            // --- OPCIONES DE NEGOCIO Y DATOS ---

            // 4. Ver Registro de Boletas
            Button(
                onClick = { navController.navigate(AppScreens.ADMIN_BOLETAS) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Boletas", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Ver Registro de Boletas")
            }

            // 5. Gestión de Usuarios (Nueva Opción Común)
            Button(
                onClick = { /* TODO: Implementar pantalla de gestión de usuarios */ },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = "Usuarios", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text("Gestionar Clientes/Usuarios")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Nota: El botón de "Cerrar Sesión de Admin" se movió a la TopAppBar
        }
    }
}