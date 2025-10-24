package com.example.aplicacion.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicacion.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuienesSomosScreen(navController: NavController) {
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
            BottomAppBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { navController.navigate(AppScreens.INICIO) }) { Text("Inicio") }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo y Misión/Visión
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(modifier = Modifier.size(100.dp)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Logo")
                        }
                    }
                    Card(modifier = Modifier.height(100.dp).weight(1f).padding(start = 16.dp)) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Misión / Visión (Texto)")
                        }
                    }
                }
            }

            // Historia
            item {
                Card(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Historia (Texto)\n\nAquí va la historia de Coffee Flowers...")
                    }
                }
            }

            // Equipo
            item {
                Card(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Equipo (Avatares)\n\n- Persona 1\n- Persona 2")
                    }
                }
            }

            // Contacto
            item {
                Card(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Contacto (Email, Tel, RRSS)\n\n- Mail: contacto@coffeeflowers.cl\n- Tel: +56 9 ...")
                    }
                }
            }
        }
    }
}