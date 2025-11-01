package com.example.aplicacion.screens

// --- 👇 1. IMPORTS MODIFICADOS 👇 ---
import androidx.compose.foundation.Image // <-- Se usa Image, no AsyncImage
import androidx.compose.ui.res.painterResource // <-- Para cargar desde 'drawable'
import com.example.aplicacion.R // <-- Importante para R.drawable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.platform.LocalContext // <-- Ya no se necesita
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion.CarritoViewModel
// import coil.compose.AsyncImage // <-- Ya no se usa
// import coil.request.ImageRequest // <-- Ya no se usa
import androidx.compose.material3.FabPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuienesSomosScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val carritoViewModel: CarritoViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiénes Somos") },
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

            // 1. Logo y Misión/Visión
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    // --- 👇 2. CÓDIGO DE IMAGEN CORRECTO 👇 ---
                    // Asumiendo que tu archivo es 'logo.jpeg',
                    // el ID es R.drawable.logo
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de Coffee Flower",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit // 'Fit' es mejor para logos
                    )

                    Column {
                        Text("Misión / Visión", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Ser la mejor cafetería de la región, ofreciendo productos de calidad " +
                                    "y un espacio acogedor para nuestros clientes.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // 2. Historia
            item {
                Text("Historia", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Coffee Flower nació en 2024 con el sueño de dos emprendedores de " +
                            "traer el mejor café de grano a la comunidad...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 3. Equipo
            item {
                Text("Equipo", style = MaterialTheme.typography.titleLarge)
                Text("Conoce a nuestro talentoso equipo de baristas.", style = MaterialTheme.typography.bodyMedium)
            }

            // 4. Contacto
            item {
                Text("Contacto", style = MaterialTheme.typography.titleLarge)
                Text("Email: contacto@coffeeflower.com\nTel: +56 9 1234 5678\nRRSS: @coffeeflower", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}