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
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.model.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    viewModel: CarritoViewModel
) {
    val carritoItems by viewModel.carrito.collectAsState()
    val total by viewModel.totalCarrito.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
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
                    Button(onClick = { navController.navigate(AppScreens.PRODUCTOS) }) { Text("Productos") }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {

                if (carritoItems.isEmpty()) {
                    item {
                        Text(
                            "Tu carrito está vacío",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                items(carritoItems) { item ->
                    CarritoItemView(
                        item = item,
                        onRestar = { viewModel.restarDelCarrito(item) },
                        onSumar = { viewModel.agregarAlCarrito(item.producto, item.opcionesSeleccionadas) },
                        onEliminar = { viewModel.eliminarDelCarrito(item) }
                    )
                }
            }

            // --- SECCIÓN DE TOTALES Y PAGAR ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    val totalOpciones = carritoItems.sumOf { item ->
                        item.opcionesSeleccionadas.values.sumOf { it.precioAdicional } * item.cantidad
                    }
                    val subtotal = total - totalOpciones

                    Text("Subtotal: $${"%.0f".format(subtotal)}")
                    if (totalOpciones > 0) {
                        Text("Adicionales: $${"%.0f".format(totalOpciones)}")
                    }

                    Text(
                        "Total: $${"%.0f".format(total)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Lógica de Pagar */ },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = carritoItems.isNotEmpty()
                    ) {
                        Text("Pagar")
                    }
                }
            }
        }
    }
}

@Composable
fun CarritoItemView(
    item: CartItem,
    onRestar: () -> Unit,
    onSumar: () -> Unit,
    onEliminar: () -> Unit
) {
    val precioBase = item.producto.precio
    val precioOpciones = item.opcionesSeleccionadas.values.sumOf { it.precioAdicional }
    val precioUnitario = precioBase + precioOpciones

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(item.producto.nombre, style = MaterialTheme.typography.titleMedium)

            item.opcionesSeleccionadas.forEach { (nombreOpcion, valorOpcion) ->
                Text(
                    "  • ${valorOpcion.nombre}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Text("Precio unitario: $${"%.0f".format(precioUnitario)}")
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onRestar,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("-") }

                    Text("${item.cantidad}", modifier = Modifier.padding(horizontal = 16.dp))

                    OutlinedButton(
                        onClick = onSumar,
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("+") }
                }

                TextButton(onClick = onEliminar) {
                    Text("Eliminar")
                }
            }
        }
    }
}