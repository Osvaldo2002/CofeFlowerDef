package com.example.aplicacion.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AuthViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuienesSomosScreen(
    navController: NavController,
    authViewModel: AuthViewModel // <-- ACEPTA EL AUTHVIEWMODEL
) {
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
            // Usamos la barra de navegación estándar
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

            // 1. Logo y Misión/Visión
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = "https://placehold.co/400x400/6F4E37/FFFFFF?text=Logo")
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                }).build()
                        ),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp)
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