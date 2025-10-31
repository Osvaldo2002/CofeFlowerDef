package com.example.aplicacion.model

import java.util.UUID

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val imagenUrl: String,
    val opciones: List<OpcionProducto> = emptyList()
)

data class OpcionProducto(
    val nombre: String,
    val valores: List<ValorOpcion>
)

data class ValorOpcion(
    val nombre: String,
    val precioAdicional: Double
)

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val producto: Producto,
    var cantidad: Int = 1,
    val opcionesSeleccionadas: Map<String, ValorOpcion> = emptyMap()
)

// --- NUEVA CLASE AÑADIDA ---
data class Boleta(
    val id: String,
    val items: List<CartItem>,
    val total: Double,
    val fecha: Long, // Guardamos la fecha como un timestamp (Long)
    val userEmail: String
)