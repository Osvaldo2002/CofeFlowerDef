package com.example.aplicacion.model

import java.util.UUID

// Representa un valor para una opción, ej: "Sin Lactosa" y su precio extra
data class ValorOpcion(
    val nombre: String,
    val precioAdicional: Double = 0.0
)

// Representa una categoría de opción, ej: "Tipo de Leche"
data class OpcionProducto(
    val nombre: String,
    val valores: List<ValorOpcion>
)

// Modelo para un producto individual, con todos los campos del formulario
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String = "",
    val precio: Double,
    val stock: Int = 0,
    val categoria: String = "General",
    val imagenUrl: String = "",
    val opciones: List<OpcionProducto> = emptyList() // Lista de personalizaciones
)

// Modelo para un ítem dentro del carrito
data class CartItem(
    // ID único para diferenciar ítems (ej: 2 Lattes con distinta leche)
    val id: String = UUID.randomUUID().toString(),
    val producto: Producto,
    var cantidad: Int = 1,
    // Mapa para guardar las selecciones, ej: {"Tipo de Leche" = "Sin Lactosa"}
    val opcionesSeleccionadas: Map<String, ValorOpcion>
)