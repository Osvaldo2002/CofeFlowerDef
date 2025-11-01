package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion.CategoriasViewModel
import com.example.aplicacion.model.Categoria
import com.example.aplicacion.AppScreens
// 🟢 IMPORTACIÓN NECESARIA
import com.example.aplicacion.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriasScreen(
    navController: NavController,
    // 🟢 CORRECCIÓN: Ahora acepta el AuthViewModel.
    authViewModel: AuthViewModel,
    // Nota: El viewModel que se pasa en MainActivity sobrescribirá este default.
    viewModel: CategoriasViewModel = viewModel()
) {
    // Observamos el StateFlow para obtener la List<Categoria> real
    val categorias by viewModel.categorias.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var categoriaAEliminar by remember { mutableStateOf<Categoria?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías 🏷️") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Botón para ir a Agregar Producto
                    Button(
                        onClick = {
                            // Asumo que esta navegación NO requiere AuthViewModel, si lo requiere,
                            // debes añadirlo a la llamada y al destino.
                            navController.navigate(AppScreens.AGREGAR_PRODUCTO)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Productos")
                        Spacer(Modifier.width(4.dp))
                        Text("Productos")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                categoriaSeleccionada = null // Asegura que es "Agregar"
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Categoría")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (categorias.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no hay categorías registradas.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categorias, key = { it.id }) { categoria ->
                        CategoriaAdminItem(
                            categoria = categoria,
                            onEditClick = {
                                categoriaSeleccionada = categoria
                                showDialog = true
                            },
                            onDeleteClick = { categoriaAEliminar = categoria }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    // --- DIÁLOGOS DE GESTIÓN (LLAMAN AL VIEWMODEL) ---

    if (showDialog) {
        CategoriaCRUDDialog(
            categoria = categoriaSeleccionada,
            onDismiss = {
                showDialog = false
                categoriaSeleccionada = null
            },
            onConfirm = { nombre ->
                if (categoriaSeleccionada == null) {
                    viewModel.agregarCategoria(nombre)
                } else {
                    viewModel.editarCategoria(categoriaSeleccionada!!, nombre)
                }
                showDialog = false
                categoriaSeleccionada = null
            }
        )
    }

    if (categoriaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { categoriaAEliminar = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar la categoría '${categoriaAEliminar!!.nombre}'?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.eliminarCategoria(categoriaAEliminar!!)
                    categoriaAEliminar = null
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { categoriaAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// --- COMPOSABLE REUTILIZABLE PARA CADA FILA ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaAdminItem(
    categoria: Categoria,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(categoria.nombre, style = MaterialTheme.typography.titleMedium) },
        trailingContent = {
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

// --- COMPOSABLE DE DIÁLOGO PARA AGREGAR/EDITAR ---

@Composable
fun CategoriaCRUDDialog(
    categoria: Categoria?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val esEdicion = categoria != null
    // Usamos 'remember(categoria)' para que el estado se reinicie si la categoría cambia
    var nombreInput by remember(categoria) { mutableStateOf(categoria?.nombre ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (esEdicion) "Modificar Categoría" else "Agregar Categoría") },
        text = {
            Column {
                Text("Nombre de la Categoría:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombreInput,
                    onValueChange = { nombreInput = it },
                    label = { Text("Ej: Postres, Café Helado...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = nombreInput.isNotBlank(),
                onClick = { onConfirm(nombreInput) }
            ) {
                Text(if (esEdicion) "Guardar Cambios" else "Agregar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}