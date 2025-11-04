package com.example.aplicacion.screens

// Imports para el buscador
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
// Imports para la barra de navegación
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
// Imports estándar
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.foundation.clickable
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aplicacion.AppScreens
import com.example.aplicacion.AuthViewModel
import com.example.aplicacion.CarritoViewModel
import com.example.aplicacion.model.Producto
import com.example.aplicacion.model.ValorOpcion
// --- IMPORTS AÑADIDOS Y CORREGIDOS PARA EL CARRUSEL ---
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background // Añadido para resolver 'Unresolved reference: background'
import com.google.accompanist.pager.ExperimentalPagerApi // Añadido para resolver anotación experimental
import com.google.accompanist.pager.HorizontalPager // Añadido para resolver HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator // Añadido para resolver HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState // Añadido para resolver rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
// --- FIN DE IMPORTS AÑADIDOS Y CORREGIDOS ---

// --- ======================================================= ---
// --- BARRA DE NAVEGACIÓN (Sin cambios) ---
// --- ======================================================= ---
@Composable
fun AppBottomBar(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val esAdmin by authViewModel.esAdmin.collectAsState()
    val userEmail by authViewModel.userEmail.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = (currentRoute == AppScreens.INICIO),
            onClick = {
                navController.navigate(AppScreens.INICIO) {
                    popUpTo(AppScreens.INICIO) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Store, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = (currentRoute == AppScreens.PRODUCTOS),
            onClick = {
                navController.navigate(AppScreens.PRODUCTOS) {
                    popUpTo(AppScreens.INICIO) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Nosotros") },
            label = { Text("Nosotros") },
            selected = (currentRoute == AppScreens.QUIENES_SOMOS),
            onClick = {
                navController.navigate(AppScreens.QUIENES_SOMOS) {
                    popUpTo(AppScreens.INICIO) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        when {
            esAdmin -> {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Panel") },
                    label = { Text("Admin") },
                    selected = (currentRoute == AppScreens.ADMIN_PANEL),
                    onClick = {
                        navController.navigate(AppScreens.ADMIN_PANEL) {
                            popUpTo(AppScreens.INICIO) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            userEmail != null -> {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Salir") },
                    label = { Text("Salir") },
                    selected = false,
                    onClick = {
                        authViewModel.logout()
                        navController.navigate(AppScreens.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            else -> {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Login, contentDescription = "Login") },
                    label = { Text("Login") },
                    selected = (currentRoute == AppScreens.LOGIN),
                    onClick = {
                        navController.navigate(AppScreens.LOGIN) {
                            popUpTo(AppScreens.INICIO) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}


// --- ======================================================= ---
// --- PANTALLA DE INICIO (MODIFICADA CON FILTRO DE CATEGORÍAS Y CARRUSEL) ---
// --- ======================================================= ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val productos by carritoViewModel.productos.collectAsState()
    var productoParaOpciones by remember { mutableStateOf<Producto?>(null) }
    var textoBusqueda by remember { mutableStateOf("") }

    // (null significa "Todos")
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    // Lógica de Búsqueda
    val productosFiltradosPorBusqueda = remember(productos, textoBusqueda) {
        if (textoBusqueda.isBlank()) {
            emptyList()
        } else {
            productos.filter { producto ->
                producto.nombre.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }

    // LÓGICA DE CATEGORÍAS
    val categorias = remember(productos) {
        productos.map { it.categoria }.distinct().sorted()
    }
    // Filtramos los productos por la categoría seleccionada
    val productosFiltradosPorCategoria = remember(productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == null) {
            productos // "Todos"
        } else {
            productos.filter { it.categoria == categoriaSeleccionada }
        }
    }

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
            AppBottomBar(
                navController = navController,
                authViewModel = authViewModel
            )
        },
        floatingActionButton = {
            CarritoFloatingButton(
                navController = navController,
                carritoViewModel = carritoViewModel
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- BUSCADOR ---
            item {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    label = { Text("Buscar productos...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    trailingIcon = {
                        if (textoBusqueda.isNotEmpty()) {
                            IconButton(onClick = { textoBusqueda = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )
            }

            // --- CONTENIDO CONDICIONAL ---
            if (textoBusqueda.isBlank()) {

                // --- VISTA POR DEFECTO (SI NO HAY BÚSQUEDA) ---

                // --- 👇 CARRUSEL DE PRODUCTOS (Reemplaza el Banner Principal estático) 👇 ---
                item {
                    ProductoCarousel(
                        productos = productos,
                        navController = navController
                    )
                }
                // --- FIN CARRUSEL ---


                // --- FILTROS DE CATEGORÍA DINÁMICOS ---
                item {
                    Text("Categorías", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        // Chip "Todos"
                        item {
                            ElevatedFilterChip(
                                selected = (categoriaSeleccionada == null),
                                onClick = { categoriaSeleccionada = null },
                                label = { Text("Todos") }
                            )
                        }

                        // Chips Dinámicos
                        items(categorias) { categoria ->
                            ElevatedFilterChip(
                                selected = (categoriaSeleccionada == categoria),
                                onClick = { categoriaSeleccionada = categoria },
                                label = { Text(categoria) }
                            )
                        }
                    }
                }

                // --- Destacados (Lista horizontal simple) ---
                item {
                    Text("Recomendados", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(productos.take(2)) { producto ->
                            ProductoCardPequeno(producto = producto, onClick = {
                                // Navegar a la vista de productos o a un detalle específico
                                navController.navigate(AppScreens.PRODUCTOS)
                            })
                        }
                    }
                }

                item {
                    // El título cambia si hay un filtro aplicado
                    val tituloLista = if (categoriaSeleccionada == null) {
                        "Todos los Productos"
                    } else {
                        "Productos de: $categoriaSeleccionada"
                    }
                    Text(tituloLista, style = MaterialTheme.typography.titleMedium)
                }

                // --- LISTA FILTRADA POR CATEGORÍA ---
                items(productosFiltradosPorCategoria, key = { it.id }) { producto ->
                    ProductoRowSimple(
                        producto = producto,
                        onAgregarClick = {
                            if (producto.opciones.isEmpty()) {
                                carritoViewModel.agregarAlCarrito(producto, emptyMap())
                            } else {
                                productoParaOpciones = producto
                            }
                        }
                    )
                }
                // Mensaje si el filtro no devuelve nada
                if (productosFiltradosPorCategoria.isEmpty() && categoriaSeleccionada != null) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay productos en esta categoría.")
                        }
                    }
                }

            } else {

                // --- VISTA DE BÚSQUEDA (SI HAY TEXTO) ---

                item {
                    Text(
                        "Resultados para \"$textoBusqueda\"",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (productosFiltradosPorBusqueda.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron productos.")
                        }
                    }
                } else {
                    // Mostramos SÓLO la lista FILTRADA por búsqueda
                    items(productosFiltradosPorBusqueda, key = { it.id }) { producto ->
                        ProductoRowSimple(
                            producto = producto,
                            onAgregarClick = {
                                if (producto.opciones.isEmpty()) {
                                    carritoViewModel.agregarAlCarrito(producto, emptyMap())
                                } else {
                                    productoParaOpciones = producto
                                }
                            }
                        )
                    }
                }
            }
        } // Fin LazyColumn

        // El diálogo de opciones funciona para ambas vistas
        if (productoParaOpciones != null) {
            OpcionesProductoDialog(
                producto = productoParaOpciones!!,
                onDismiss = {
                    productoParaOpciones = null
                },
                onConfirm = { opcionesElegidas ->
                    carritoViewModel.agregarAlCarrito(productoParaOpciones!!, opcionesElegidas)
                    productoParaOpciones = null
                }
            )
        }

    } // Fin Scaffold
}

// --- ======================================================= ---
// --- 👇 NUEVO COMPOSABLE PARA EL CARRUSEL ANIMADO 👇 ---
// --- ======================================================= ---
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductoCarousel(
    productos: List<Producto>,
    navController: NavController
) {
    // Tomamos los primeros 5 productos para el carrusel, o todos si hay menos
    val items = remember(productos) { productos.take(5) }
    if (items.isEmpty()) return // No mostrar si no hay productos

    val pagerState = rememberPagerState(initialPage = 0)

    // Lógica para la animación automática (rota cada 3 segundos)
    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(3000)
            yield()
            val nextPage = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        // 1. Carrusel de Imágenes
        HorizontalPager(
            count = items.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Altura del carrusel
                .clip(RoundedCornerShape(12.dp)) // Bordes redondeados
        ) { page ->
            val producto = items[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Navega a la vista de productos al hacer clic en un slide
                        navController.navigate(AppScreens.PRODUCTOS)
                    }
            ) {
                Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data = producto.imagenUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = producto.nombre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Superposición de texto para el producto destacado
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            // Fondo semitransparente para mejor legibilidad
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(topEnd = 8.dp))
                            .padding(4.dp)
                    ) {
                        Text(
                            producto.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Precio: $${"%.0f".format(producto.precio)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. Indicadores de página (los puntos)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = items.size,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}


// --- ======================================================= ---
// --- COMPOSABLES INTERNOS (Sin cambios) ---
// --- ======================================================= ---
@Composable
fun ProductoCardPequeno(producto: Producto, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.size(160.dp)) {
        Column {
            AsyncImage(model = producto.imagenUrl, contentDescription = producto.nombre, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(100.dp))
            Text(producto.nombre, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            Text("$${"%.0f".format(producto.precio)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 8.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoRowSimple(producto: Producto, onAgregarClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(producto.nombre) },
        supportingContent = { Text(producto.descripcion, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text("$${"%.0f".format(producto.precio)}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Button(onClick = onAgregarClick, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("Agregar +")
                }
            }
        },
        leadingContent = {
            AsyncImage(model = producto.imagenUrl, contentDescription = producto.nombre, contentScale = ContentScale.Crop, modifier = Modifier.size(64.dp))
        }
    )
    Divider()
}

// --- ======================================================= ---
// --- DIÁLOGO DE OPCIONES (Sin cambios) ---
// --- ======================================================= ---
@Composable
fun OpcionesProductoDialog(producto: Producto, onDismiss: () -> Unit, onConfirm: (Map<String, ValorOpcion>) -> Unit) {
    val defaultOpciones = producto.opciones.associate { opcion -> opcion.nombre to opcion.valores.first() }
    var opcionesElegidas by remember { mutableStateOf(defaultOpciones) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elige tus opciones para ${producto.nombre}") },
        text = {
            LazyColumn {
                items(producto.opciones) { opcionProducto ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(opcionProducto.nombre, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        opcionProducto.valores.forEach { valorOpcion ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { opcionesElegidas = opcionesElegidas + (opcionProducto.nombre to valorOpcion) }.padding(vertical = 4.dp)) {
                                RadioButton(selected = opcionesElegidas[opcionProducto.nombre] == valorOpcion, onClick = { opcionesElegidas = opcionesElegidas + (opcionProducto.nombre to valorOpcion) })
                                Text(valorOpcion.nombre, modifier = Modifier.weight(1f))
                                if (valorOpcion.precioAdicional > 0) {
                                    Text(text = "+ $${"%.0f".format(valorOpcion.precioAdicional)}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(opcionesElegidas) }) { Text("Agregar al carrito") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// --- ======================================================= ---
// --- BOTÓN FLOTANTE (Sin cambios) ---
// --- ======================================================= ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoFloatingButton(
    navController: NavController,
    carritoViewModel: CarritoViewModel
) {
    val carrito by carritoViewModel.carrito.collectAsState()
    val totalItems = carrito.sumOf { it.cantidad }

    BadgedBox(
        badge = {
            if (totalItems > 0) {
                Badge {
                    Text(totalItems.toString())
                }
            }
        }
    ) {
        FloatingActionButton(
            onClick = { navController.navigate(AppScreens.CARRITO) },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Ver Carrito",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}