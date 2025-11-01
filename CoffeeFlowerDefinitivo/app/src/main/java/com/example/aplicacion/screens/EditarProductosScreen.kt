package com.example.aplicacion.screens

// --- 1. Imports para la galería y vista previa ---
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.FabPosition
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
// --- Fin Imports ---

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion.AppScreens
import com.example.aplicacion.AuthViewModel // 🟢 Importación necesaria
import com.example.aplicacion.CarritoViewModel // 🟢 Importación necesaria
import com.example.aplicacion.CategoriasViewModel // 🟢 Importación necesaria
import com.example.aplicacion.model.Categoria
import com.example.aplicacion.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    navController: NavController,
    // 🟢 ESTOS PARÁMETROS COINCIDEN CON LA LLAMADA EN MainActivity.kt (Línea 147) 🟢
    viewModel: CarritoViewModel,
    authViewModel: AuthViewModel,
    productoId: Int?
) {
    // --- 3. Obtener el producto (tu lógica está bien) ---
    val productoAEditar = remember(productoId) {
        viewModel.getProductoPorId(productoId ?: -1)
    }

    // --- 4. Estados del formulario (cargados desde 'productoAEditar') ---
    // NOTA: Si CategoriasViewModel no es un parámetro de la función, debe obtenerse aquí:
    val categoriasViewModel: CategoriasViewModel = viewModel()
    val categoriasDisponibles by categoriasViewModel.categorias.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // --- 5. Lógica de Dropdown de Categoría (en lugar de OutlinedTextField) ---
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val categoriasVacias = categoriasDisponibles.isEmpty()

    // --- 6. Cargar datos en los estados cuando 'productoAEditar' esté listo ---
    LaunchedEffect(productoAEditar, categoriasDisponibles) {
        if (productoAEditar != null) {
            nombre = productoAEditar.nombre
            descripcion = productoAEditar.descripcion
            precio = "%.0f".format(productoAEditar.precio)
            stock = productoAEditar.stock.toString()
            fotoUrl = productoAEditar.imagenUrl

            // Pre-selecciona la categoría en el dropdown
            if (categoriasDisponibles.isNotEmpty()) {
                categoriaSeleccionada = categoriasDisponibles.find { it.nombre == productoAEditar.categoria }
            }
        }
    }

    // --- 7. Lanzador de fotos (idéntico a AgregarProductoScreen) ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUrl = uri.toString() // Actualiza el estado 'fotoUrl'
        }
    }

    // NOTA: Asumo que AppBottomBar y CarritoFloatingButton han sido definidos en otras partes.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productoAEditar != null) "Editar: ${productoAEditar.nombre}" else "Editar Producto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        // --- 8. Usamos la barra de navegación y el FAB consistentes ---
        bottomBar = {
            // Nota: Se asume que AppBottomBar está definida y acepta navController y authViewModel.
            AppBottomBar(
                navController = navController,
                authViewModel = authViewModel
            )
        },
        floatingActionButton = {
            // Nota: Se asume que CarritoFloatingButton está definida y acepta navController y carritoViewModel.
            CarritoFloatingButton(
                navController = navController,
                carritoViewModel = viewModel
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        if (productoId == null || productoAEditar == null) {
            // ... (Tu manejo de error está perfecto) ...
            Column(
                modifier = Modifier.padding(innerPadding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Error: Producto no encontrado.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ... (Campos Nombre, Descripcion, Precio, Stock - Sin cambios) ...
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
                        onValueChange = { precio = it.filter { char -> char.isDigit() || char == '.' } }, // <-- Corrección de filtro
                        label = { Text("Precio *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // <-- Corrección de teclado
                        prefix = { Text("$") },
                        isError = error?.contains("Precio") == true
                    )
                }
                item {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it.filter { char -> char.isDigit() } }, // <-- Corrección de filtro
                        label = { Text("Stock") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = error?.contains("Stock") == true
                    )
                }

                // --- 9. DROPDOWN DE CATEGORÍA ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.TopStart)
                    ) {
                        OutlinedTextField(
                            value = when {
                                categoriasVacias -> "No hay categorías"
                                categoriaSeleccionada != null -> categoriaSeleccionada!!.nombre
                                else -> "Seleccione una categoría *"
                            },
                            onValueChange = { },
                            label = { Text("Categoría *") },
                            readOnly = true,
                            trailingIcon = {
                                if (!categoriasVacias) {
                                    Icon(
                                        Icons.Filled.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        Modifier.clickable { expanded = true }
                                    )
                                }
                            },
                            isError = error?.contains("Categoría") == true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !categoriasVacias) { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            categoriasDisponibles.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = {
                                        categoriaSeleccionada = categoria
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // --- 10. SECCIÓN DE FOTO ---
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Foto (Opcional)",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            AsyncImage(
                                model = fotoUrl.ifEmpty {
                                    "https://placehold.co/600x400/CCCCCC/FFFFFF?text=Vista+Previa"
                                },
                                contentDescription = "Vista previa del producto",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Text("Cambiar Foto de Galería")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("o ingrese la URL:")
                    }
                }
                item {
                    OutlinedTextField(
                        value = fotoUrl,
                        onValueChange = { fotoUrl = it },
                        label = { Text("URL de la Foto o Uri") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (error != null) {
                    item {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                    }
                }

                // --- 11. LÓGICA DE GUARDAR (Actualizada) ---
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = {
                            val precioDouble = precio.toDoubleOrNull()
                            val stockInt = stock.toIntOrNull() ?: 0

                            if (nombre.isBlank()) {
                                error = "Error: El 'Nombre' es obligatorio."
                            } else if (precioDouble == null || precioDouble <= 0) {
                                error = "Error: El 'Precio' debe ser un número válido."
                            } else if (categoriaSeleccionada == null) {
                                error = "Error: Debe seleccionar una Categoría."
                            } else {
                                error = null
                                // Usamos .copy() para crear el producto actualizado
                                val productoActualizado = productoAEditar.copy(
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    precio = precioDouble,
                                    stock = stockInt,
                                    categoria = categoriaSeleccionada!!.nombre, // <-- Usa el dropdown
                                    imagenUrl = fotoUrl
                                    // 'opciones' y 'id' se mantienen
                                )
                                viewModel.actualizarProducto(productoActualizado)
                                navController.popBackStack()
                            }
                        }) {
                            Text("Guardar Cambios")
                        }
                        OutlinedButton(onClick = { navController.popBackStack() }) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}