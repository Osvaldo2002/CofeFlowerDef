package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.model.Producto
import com.example.aplicacion.model.OpcionProducto
import com.example.aplicacion.model.ValorOpcion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    viewModel: CarritoViewModel
) {
    val productos by viewModel.productos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
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
                    Button(onClick = { navController.navigate(AppScreens.CARRITO) }) { Text("Carrito") }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                Button(onClick = { navController.navigate(AppScreens.AGREGAR_PRODUCTO) }) {
                    Text("Agregar Nuevo Producto")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(productos) { producto ->
                ProductoItem(
                    producto = producto,
                    onAgregarClick = { productoSeleccionado, opcionesElegidas ->
                        viewModel.agregarAlCarrito(productoSeleccionado, opcionesElegidas)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onAgregarClick: (Producto, Map<String, ValorOpcion>) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val opcionesPorDefecto = producto.opciones.associate { it.nombre to it.valores.first() }
    var opcionesSeleccionadas by remember { mutableStateOf(opcionesPorDefecto) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(producto.descripcion, style = MaterialTheme.typography.bodySmall)
                Text("$${"%.0f".format(producto.precio)}", style = MaterialTheme.typography.bodyMedium)
            }

            Button(onClick = {
                if (producto.opciones.isEmpty()) {
                    onAgregarClick(producto, emptyMap())
                } else {
                    opcionesSeleccionadas = opcionesPorDefecto
                    mostrarDialogo = true
                }
            }) {
                Text("Agregar +")
            }
        }
    }

    if (mostrarDialogo) {
        OpcionesProductoDialog(
            producto = producto,
            opcionesSeleccionadas = opcionesSeleccionadas,
            onOpcionChange = { nombreOpcion, nuevoValor ->
                opcionesSeleccionadas = opcionesSeleccionadas.toMutableMap().apply {
                    this[nombreOpcion] = nuevoValor
                }
            },
            onDismiss = { mostrarDialogo = false },
            onConfirm = {
                onAgregarClick(producto, opcionesSeleccionadas)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
fun OpcionesProductoDialog(
    producto: Producto,
    opcionesSeleccionadas: Map<String, ValorOpcion>,
    onOpcionChange: (String, ValorOpcion) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Personaliza tu ${producto.nombre}") },
        text = {
            LazyColumn {
                items(producto.opciones) { opcion ->
                    OpcionItem(
                        opcion = opcion,
                        valorSeleccionado = opcionesSeleccionadas[opcion.nombre],
                        onValorSelected = { nuevoValor ->
                            onOpcionChange(opcion.nombre, nuevoValor)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun OpcionItem(
    opcion: OpcionProducto,
    valorSeleccionado: ValorOpcion?,
    onValorSelected: (ValorOpcion) -> Unit
) {
    Column {
        Text(opcion.nombre, style = MaterialTheme.typography.titleMedium)
        opcion.valores.forEach { valor ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (valor == valorSeleccionado),
                        onClick = { onValorSelected(valor) }
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (valor == valorSeleccionado),
                    onClick = { onValorSelected(valor) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                val precioExtra = if (valor.precioAdicional > 0) " (+$${"%.0f".format(valor.precioAdicional)})" else ""
                Text(valor.nombre + precioExtra)
            }
        }
    }
}