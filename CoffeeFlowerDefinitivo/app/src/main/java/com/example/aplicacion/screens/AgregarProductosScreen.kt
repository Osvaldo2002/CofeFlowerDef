package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens
import com.example.aplicacion.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    navController: NavController,
    viewModel: CarritoViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Producto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { navController.navigate(AppScreens.INICIO) }) { Text("Inicio") }
                    Button(onClick = { navController.navigate(AppScreens.PRODUCTOS) }) { Text("Ver Productos") }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error?.contains("Nombre") == true,
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("$") },
                    isError = error?.contains("Precio") == true
                )
            }
            item {
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = error?.contains("Stock") == true
                )
            }
            item {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = fotoUrl,
                    onValueChange = { fotoUrl = it },
                    label = { Text("URL de la Foto") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (error != null) {
                item {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        val precioDouble = precio.toDoubleOrNull()
                        val stockInt = stock.toIntOrNull() ?: 0

                        if (nombre.isBlank()) {
                            error = "Error: El 'Nombre' es obligatorio."
                        } else if (precioDouble == null || precioDouble <= 0) {
                            error = "Error: El 'Precio' debe ser un número válido."
                        } else {
                            error = null
                            viewModel.agregarNuevoProducto(
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precioDouble,
                                stock = stockInt,
                                categoria = if (categoria.isBlank()) "General" else categoria,
                                imagenUrl = fotoUrl
                            )
                            navController.popBackStack() // Volver a la pantalla anterior
                        }
                    }) {
                        Text("Guardar")
                    }
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}