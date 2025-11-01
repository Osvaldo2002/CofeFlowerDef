package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens
import com.example.aplicacion.AuthViewModel
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.model.CartItem
import com.example.aplicacion.model.ValorOpcion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    viewModel: CarritoViewModel,
    authViewModel: AuthViewModel
) {
    val carrito by viewModel.carrito.collectAsState()
    val total by viewModel.totalCarrito.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                // ... (colores, etc. sin cambios)
            )
        },
        bottomBar = {
            // --- 👇 MODIFICACIÓN AQUÍ 👇 ---
            // La llamada ahora es más simple, sin el carritoViewModel
            AppBottomBar(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        // --- NO AÑADIMOS 'floatingActionButton' AQUÍ ---
        // (No tiene sentido mostrar un botón "Ir al Carrito"
        //  cuando ya estás en el carrito)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // ... (El resto de tu pantalla CarritoScreen queda exactamente igual) ...
            if (carrito.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineSmall)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(carrito, key = { it.id }) { item ->
                        CarritoItem(
                            item = item,
                            onSumar = { viewModel.sumarAlCarrito(item) },
                            onRestar = { viewModel.restarDelCarrito(item) },
                            onEliminar = { viewModel.eliminarDelCarrito(item) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text("$${"%.0f".format(total)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (userEmail != null) {
                                    val boletaId = viewModel.generarBoleta(userEmail!!)
                                    navController.navigate(AppScreens.BOLETA_GENERADA + "/$boletaId") {
                                        popUpTo(AppScreens.INICIO)
                                    }
                                } else {
                                    navController.navigate(AppScreens.LOGIN)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (userEmail != null) "Pagar Ahora" else "Iniciar Sesión para Pagar")
                        }
                    }
                }
            }
        }
    }
}

// ... (El Composable CarritoItem queda exactamente igual) ...
@Composable
fun CarritoItem(
    item: CartItem,
    onSumar: () -> Unit,
    onRestar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (item.opcionesSeleccionadas.isNotEmpty()) {
                        item.opcionesSeleccionadas.forEach { (nombreOpcion, valorOpcion) ->
                            Text(text = "  · ${valorOpcion.nombre}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                val precioItem = (item.producto.precio + item.opcionesSeleccionadas.values.sumOf { it.precioAdicional }) * item.cantidad
                Text(text = "$${"%.0f".format(precioItem)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = onRestar, modifier = Modifier.size(40.dp), contentPadding = PaddingValues(0.dp)) { Text("-") }
                    Text(text = "${item.cantidad}", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodyLarge)
                    OutlinedButton(onClick = onSumar, modifier = Modifier.size(40.dp), contentPadding = PaddingValues(0.dp)) { Text("+") }
                }
                TextButton(onClick = onEliminar) {
                    Text("Eliminar")
                }
            }
        }
    }
}