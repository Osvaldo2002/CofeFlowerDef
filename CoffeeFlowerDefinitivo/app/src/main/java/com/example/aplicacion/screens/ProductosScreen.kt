package com.example.aplicacion.screens

// --- 👇 1. AÑADE ESTE IMPORT ---
import androidx.compose.material3.FabPosition // Para el botón flotante

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
    // val userEmail by authViewModel.userEmail.collectAsState() // Ya no se usa en la nueva barra

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
        // --- 👇 2. SE REEMPLAZA EL 'bottomBar' ANTIGUO POR EL NUEVO ---
        bottomBar = {
            // Usamos la misma barra de navegación profesional de InicioScreen
            AppBottomBar(
                navController = navController,
                authViewModel = authViewModel
            )
        },
        // --- 👇 3. SE AÑADE EL BOTÓN FLOTANTE ---
        floatingActionButton = {
            // Usamos el mismo botón flotante de InicioScreen
            CarritoFloatingButton(
                navController = navController,
                carritoViewModel = viewModel
            )
        },
        floatingActionButtonPosition = FabPosition.End // Lo posiciona a la derecha
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding) // <-- Usamos el padding del Scaffold
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

            items(productos, key = { it.id }) { producto -> // <-- Añadido key para mejor rendimiento
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

// --- ======================================================= ---
// --- EL RESTO DEL ARCHIVO (ProductoItem, Diálogos, etc.) ---
// --- NO NECESITA NINGÚN CAMBIO ---
// --- ======================================================= ---

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
                Text(producto.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis) // <-- Límite de líneas
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
