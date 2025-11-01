package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.CarritoViewModel
// 🟢 IMPORTACIÓN NECESARIA
import com.example.aplicacion.AuthViewModel
import com.example.aplicacion.model.Boleta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBoletasScreen(
    navController: NavController,
    viewModel: CarritoViewModel,
    // 🟢 CORRECCIÓN: Agregar el AuthViewModel que se pasa desde MainActivity
    authViewModel: AuthViewModel
) {
    val boletas by viewModel.boletas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Boletas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        // El ícono moderno para volver atrás
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
                // Si la pantalla de AdminBoletas necesitara el botón de logout,
                // las acciones irían aquí usando el authViewModel.
            )
        }
    ) { innerPadding ->
        if (boletas.isEmpty()) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no se han generado boletas.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(boletas) { boleta ->
                    BoletaItem(boleta = boleta)
                }
            }
        }
    }
}

@Composable
fun BoletaItem(boleta: Boleta) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Boleta ID: ${boleta.id}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Cliente: ${boleta.userEmail}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Fecha: ${boleta.fecha.toFormattedDateString()}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Total: $${"%.0f".format(boleta.total)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            boleta.items.forEach { item ->
                Text(
                    "  · ${item.cantidad}x ${item.producto.nombre}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// Función helper para formatear la fecha
fun Long.toFormattedDateString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}