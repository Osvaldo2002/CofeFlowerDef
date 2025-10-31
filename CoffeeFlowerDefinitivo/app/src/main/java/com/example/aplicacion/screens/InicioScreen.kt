package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

// CAMBIO DE IMPORTACIÓN: Usaremos el composable AsyncImage de Coil
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aplicacion.AppScreens
import com.example.aplicacion.AuthViewModel
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.model.Producto

// --- ======================================================= ---
// --- BARRA DE NAVEGACIÓN REUTILIZABLE (LA DEFINIMOS AQUÍ) ---
// --- ======================================================= ---
@Composable
fun AppBottomBar(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val esAdmin by authViewModel.esAdmin.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), // Padding ajustado
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botones principales (siempre visibles)
            Button(onClick = { navController.navigate(AppScreens.INICIO) { popUpTo(0) } }) { Text("Inicio") }
            Button(onClick = { navController.navigate(AppScreens.PRODUCTOS) }) { Text("Productos") }
            Button(onClick = { navController.navigate(AppScreens.CARRITO) }) { Text("Carrito") }

            // Lógica Centralizada de Navegación del Usuario/Admin
            when {
                // Opción 1: Es Administrador
                esAdmin -> {
                    Button(
                        onClick = { navController.navigate(AppScreens.ADMIN_PANEL) },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                    ) {
                        Text("Admin Panel")
                    }
                }
                // Opción 2: Está Logeado (pero no es Admin)
                userEmail != null -> {
                    Button(onClick = { authViewModel.logout() }) {
                        Text("Salir")
                    }
                }
                // Opción 3: No está Logeado
                else -> {
                    Button(onClick = { navController.navigate(AppScreens.LOGIN) }) {
                        Text("Login")
                    }
                }
            }
        }
    }
}


// --- ======================================================= ---
// --- PANTALLA DE INICIO ---
// --- ======================================================= ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    // Si el usuario es administrador, redirige al Panel Admin si no está ya en la pantalla de inicio
    val esAdmin by authViewModel.esAdmin.collectAsState()
    // HE QUITADO LA REDIRECCIÓN AUTOMÁTICA AQUÍ para evitar un bucle de navegación.
    // La UX de la barra inferior ya es clara.

    val productos by carritoViewModel.productos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido a Coffee Flower") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            AppBottomBar(navController = navController, authViewModel = authViewModel)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Buscar productos...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                ) {
                    // === CORRECCIÓN COIL 1: Usando AsyncImage para el Banner ===
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data = "https://placehold.co/1200x400/6F4E37/FFFFFF?text=Banner+Principal")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Banner Principal",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            item {
                Text("Categorías", style = MaterialTheme.typography.titleMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Cafés", "Pastelería", "Té", "Otros")) { categoria ->
                        ElevatedFilterChip(
                            selected = false,
                            onClick = { /* TODO: Lógica de filtro */ },
                            label = { Text(categoria) }
                        )
                    }
                }
            }

            item {
                Text("Destacados", style = MaterialTheme.typography.titleMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(productos.take(2)) { producto ->
                        ProductoCardPequeno(producto = producto, onClick = {
                            navController.navigate(AppScreens.PRODUCTOS)
                        })
                    }
                }
            }

            item {
                Text("Todos los Productos", style = MaterialTheme.typography.titleMedium)
            }
            items(productos) { producto ->
                ProductoRowSimple(
                    producto = producto,
                    onAgregarClick = {
                        carritoViewModel.agregarAlCarrito(producto, emptyMap())
                    }
                )
            }
        }
    }
}

// --- ======================================================= ---
// --- COMPOSABLES INTERNOS DE INICIO (Con corrección Coil) ---
// --- ======================================================= ---

@Composable
fun ProductoCardPequeno(producto: Producto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(160.dp)
    ) {
        Column {
            // === CORRECCIÓN COIL 2: Usando AsyncImage para la tarjeta de producto ===
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Text(
                producto.nombre,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text(
                "$${"%.0f".format(producto.precio)}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoRowSimple(
    producto: Producto,
    onAgregarClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(producto.nombre) },

        supportingContent = {
            Text(
                producto.descripcion,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },

        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text("$${"%.0f".format(producto.precio)}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onAgregarClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Agregar +")
                }
            }
        },
        leadingContent = {
            // === CORRECCIÓN COIL 3: Usando AsyncImage para la fila de producto ===
            AsyncImage(
                model = producto.imagenUrl,
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp)
            )
        }
    )
    Divider()
}