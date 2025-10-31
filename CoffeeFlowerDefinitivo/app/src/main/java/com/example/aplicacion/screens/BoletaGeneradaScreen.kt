package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoletaGeneradaScreen(
    navController: NavController,
    boletaId: String?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compra Finalizada") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("¡Gracias por tu compra!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tu boleta ha sido generada con éxito.", style = MaterialTheme.typography.bodyLarge)
            Text("ID de Boleta: $boletaId", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                navController.navigate(AppScreens.INICIO) {
                    popUpTo(0) // Limpiamos toda la pila de navegación
                }
            }) {
                Text("Volver al Inicio")
            }
        }
    }
}