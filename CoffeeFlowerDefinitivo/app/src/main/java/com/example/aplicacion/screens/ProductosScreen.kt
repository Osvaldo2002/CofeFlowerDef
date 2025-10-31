package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens
import com.example.aplicacion.AuthViewModel
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.model.Producto
import com.example.aplicacion.model.OpcionProducto
import com.example.aplicacion.model.ValorOpcion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    viewModel: CarritoViewModel,
    authViewModel: AuthViewModel
) {
    val productos by viewModel.productos.collectAsState()
    val esAdmin by authViewModel.esAdmin.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()

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
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Fila 1: Botones de navegación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { navController.navigate(AppScreens.INICIO) }) { Text("Inicio") }
                        Button(onClick = { navController.navigate(AppScreens.CARRITO) }) { Text("Carrito") }

                        if (esAdmin) {
                            OutlinedButton(onClick = { navController.navigate(AppScreens.ADMIN_PANEL) }) {
                                Text("Panel Admin")
                            }
                        } else if (userEmail != null) {
                            TextButton(onClick = { authViewModel.logout() }) {
                                Text("Cerrar Sesión")
                            }
                        } else {
                            OutlinedButton(onClick = { navController.navigate(AppScreens.LOGIN) }) {
                                Text("Login")
                            }
                        }
                    }

                    if (userEmail != null && !esAdmin) {
                        Text(
                            text = "Logeado como: $userEmail",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding) // <-- AHORA SÍ USAMOS EL PADDING
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (esAdmin) {
                item {
                    Button(onClick = { navController.navigate(AppScreens.AGREGAR_PRODUCTO) }) {
                        Text("Agregar Nuevo Producto")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            items(productos) { producto ->
                // --- ESTA ES LA FUNCIÓN QUE DABA ERROR ---
                ProductoItem(
                    producto = producto,
                    onAgregarClick = { productoSeleccionado, opcionesElegidas ->
                        viewModel.agregarAlCarrito(productoSeleccionado, opcionesElegidas)
                    },
                    esAdmin = esAdmin,
                    onEliminarClick = {
                        viewModel.eliminarProducto(producto.id)
                    },
                    onModificarClick = {
                        navController.navigate(AppScreens.EDITAR_PRODUCTO + "/${producto.id}")
                    }
                )
            }
        }
    }
}

// --- === ESTA ES LA PARTE QUE PROBABLEMENTE FALTABA === ---

@Composable
fun ProductoItem(
    producto: Producto,
    onAgregarClick: (Producto, Map<String, ValorOpcion>) -> Unit,
    esAdmin: Boolean,
    onEliminarClick: () -> Unit,
    onModificarClick: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarDialogoBorrar by remember { mutableStateOf(false) }

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

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

                if (esAdmin) {
                    OutlinedButton(onClick = onModificarClick) {
                        Text("Modificar")
                    }
                    Button(
                        onClick = { mostrarDialogoBorrar = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }

    // --- Diálogo para agregar producto (opciones) ---
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

    // --- Diálogo de confirmación para Borrar ---
    if (mostrarDialogoBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar '${producto.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        onEliminarClick()
                        mostrarDialogoBorrar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrar = false }) {
                    Text("Cancelar")
                }
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