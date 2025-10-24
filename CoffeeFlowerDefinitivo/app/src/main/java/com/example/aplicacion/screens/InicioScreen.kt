package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens // <-- LÍNEA CORREGIDA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coffee Flowers - Inicio") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            // Barra de navegación inferior
            BottomAppBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { /* Ya estamos en Inicio */ }) { Text("Inicio") }
                    Button(onClick = { navController.navigate(AppScreens.PRODUCTOS) }) { Text("Productos") }
                    Button(onClick = { navController.navigate(AppScreens.LOGIN) }) { Text("Login") }
                    Button(onClick = { navController.navigate(AppScreens.QUIENES_SOMOS) }) { Text("Nosotros") }
                }
            }
        }
    ) { innerPadding ->
        // LazyColumn permite que el contenido sea scrolleable
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre elementos
        ) {
            // 1. Barra de Búsqueda (TextField)
            item {
                var searchQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar productos...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 2. Carrusel / Banner
            item {
                Card(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Carrusel / Banner")
                    }
                }
            }

            // 3. Categorías (A y B)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Card(modifier = Modifier.size(150.dp, 100.dp)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Categoría A")
                        }
                    }
                    Card(modifier = Modifier.size(150.dp, 100.dp)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Categoría B")
                        }
                    }
                }
            }

            // 4. Destacados (1 y 2)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Card(modifier = Modifier.size(150.dp, 100.dp)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Destacado 1")
                        }
                    }
                    Card(modifier = Modifier.size(150.dp, 100.dp)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Destacado 2")
                        }
                    }
                }
            }

            // 5. Listado resumido
            item { Text("Listado resumido (LazyColumn)", style = MaterialTheme.typography.titleMedium) }

            // Items de ejemplo para la lista
            items(10) { index ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("Producto N° ${index + 1}", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}