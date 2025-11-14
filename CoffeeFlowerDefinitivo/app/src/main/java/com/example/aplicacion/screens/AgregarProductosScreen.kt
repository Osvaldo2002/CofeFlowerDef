package com.example.aplicacion.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
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
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.CategoriasViewModel
import com.example.aplicacion.model.Categoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    navController: NavController,
    viewModel: CarritoViewModel
) {
    val categoriasViewModel: CategoriasViewModel = viewModel()
    val categoriasDisponibles by categoriasViewModel.categorias.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var fotoUrl by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    var categoriaSeleccionada by remember {
        mutableStateOf<Categoria?>(categoriasDisponibles.firstOrNull())
    }
    var expanded by remember { mutableStateOf(false) }
    val categoriasVacias = categoriasDisponibles.isEmpty()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUrl = uri.toString()
        }
    }

    LaunchedEffect(categoriasDisponibles) {
        if (categoriaSeleccionada == null) {
            categoriaSeleccionada = categoriasDisponibles.firstOrNull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Producto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
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
                    onValueChange = { precio = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Precio *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    prefix = { Text("$") },
                    isError = error?.contains("Precio") == true,
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it.filter { char -> char.isDigit() } },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = error?.contains("Stock") == true,
                    singleLine = true
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    OutlinedTextField(
                        value = when {
                            categoriasVacias -> "No hay categorías (Admin Panel)"
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
                            model = fotoUrl.ifEmpty { "https://placehold.co/600x400/CCCCCC/FFFFFF?text=Vista+Previa" },
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
                        Text("Seleccionar Foto de Galería")
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
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                        } else if (categoriaSeleccionada == null) {
                            error = "Error: Debe seleccionar una Categoría."
                        } else {
                            error = null
                            viewModel.agregarNuevoProducto(
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precioDouble,
                                stock = stockInt,
                                categoria = categoriaSeleccionada!!.nombre,
                                imagenUrl = fotoUrl
                            )
                            navController.popBackStack()
                        }
                    },
                        enabled = nombre.isNotBlank() && precio.toDoubleOrNull() != null && categoriaSeleccionada != null
                    ) {
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