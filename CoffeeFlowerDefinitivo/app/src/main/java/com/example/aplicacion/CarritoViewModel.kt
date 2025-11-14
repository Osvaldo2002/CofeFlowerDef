package com.example.aplicacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion.model.*
import com.example.aplicacion.R // Importar la clase R del proyecto
import kotlinx.coroutines.flow.*
import java.util.UUID

class CarritoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    private val _boletas = MutableStateFlow<List<Boleta>>(emptyList())
    val boletas: StateFlow<List<Boleta>> = _boletas.asStateFlow()

    val totalCarrito: StateFlow<Double> = _carrito.map { items ->
        items.sumOf { item ->
            val precioBase = item.producto.precio
            val precioOpciones = item.opcionesSeleccionadas.values.sumOf { it.precioAdicional }
            (precioBase + precioOpciones) * item.cantidad
        }
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000L),
        initialValue = 0.0
    )

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        val opcionLeche = OpcionProducto(
            "Tipo de Leche", listOf(
                ValorOpcion("Leche Entera", 0.0),
                ValorOpcion("Leche Sin Lactosa", 200.0),
                ValorOpcion("Leche de Almendras", 300.0)
            )
        )

        _productos.value = listOf(
            Producto(id = 1, nombre = "Capuchino", descripcion = "Café espresso con leche vaporizada y espuma.", precio = 3200.0, stock = 100, categoria = "Cafés", imagenResId = R.drawable.capuchino, opciones = listOf(opcionLeche)),
            Producto(id = 2, nombre = "Mokachino", descripcion = "Capuchino con un toque de chocolate.", precio = 3500.0, stock = 100, categoria = "Cafés", imagenResId = R.drawable.mokachino, opciones = listOf(opcionLeche)),
            Producto(id = 3, nombre = "Latte", descripcion = "Café espresso con más leche vaporizada que espuma.", precio = 3000.0, stock = 100, categoria = "Cafés", imagenResId = R.drawable.latte, opciones = listOf(opcionLeche)),
            Producto(id = 4, nombre = "Americano", descripcion = "Café espresso diluido con agua caliente.", precio = 2200.0, stock = 100, categoria = "Cafés", imagenResId = R.drawable.americano),
            Producto(id = 5, nombre = "Expresso", descripcion = "Café puro y concentrado.", precio = 2000.0, stock = 100, categoria = "Cafés", imagenResId = R.drawable.expresso),
            Producto(id = 6, nombre = "Cortado", descripcion = "Café espresso con una pequeña cantidad de leche.", precio = 2300.0, stock = 100, categoria = "Cafés", imagenResId = R.drawable.cortado, opciones = listOf(opcionLeche)),
            Producto(id = 7, nombre = "Pastel de Chocolate", descripcion = "Una rebanada de pastel de chocolate húmedo.", precio = 3800.0, stock = 50, categoria = "Pastelería", imagenResId = R.drawable.pastel_chocolate),
            Producto(id = 8, nombre = "Té Matcha Latte", descripcion = "Té verde matcha ceremonial con leche vaporizada.", precio = 3900.0, stock = 80, categoria = "Té e Infusiones", imagenResId = R.drawable.te_matcha_latte, opciones = listOf(opcionLeche)),
            Producto(id = 9, nombre = "Té Helado de Frambuesa", descripcion = "Té negro helado refrescante con un toque de frambuesa.", precio = 2800.0, stock = 100, categoria = "Té e Infusiones", imagenResId = R.drawable.te_helado_frambuesa),
            Producto(id = 10, nombre = "Galleta con Chips de Chocolate", descripcion = "Galleta grande y suave con chips de chocolate.", precio = 1800.0, stock = 150, categoria = "Dulces", imagenResId = R.drawable.galleta_chocolate)
        )
    }

    fun agregarAlCarrito(producto: Producto, opciones: Map<String, ValorOpcion>) {
        _carrito.update { carritoActual ->
            val itemExistente = carritoActual.find { it.producto.id == producto.id && it.opcionesSeleccionadas == opciones }

            if (itemExistente != null) {
                carritoActual.map { if (it.id == itemExistente.id) it.copy(cantidad = it.cantidad + 1) else it }
            } else {
                carritoActual + CartItem(producto = producto, cantidad = 1, opcionesSeleccionadas = opciones)
            }
        }
    }

    fun restarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual ->
            carritoActual.mapNotNull { itemEnLista ->
                if (itemEnLista.id == item.id) {
                    if (itemEnLista.cantidad > 1) itemEnLista.copy(cantidad = itemEnLista.cantidad - 1) else null
                } else {
                    itemEnLista
                }
            }
        }
    }

    fun sumarAlCarrito(item: CartItem) {
        _carrito.update { carritoActual ->
            carritoActual.map { itemEnLista -> if (itemEnLista.id == item.id) itemEnLista.copy(cantidad = itemEnLista.cantidad + 1) else itemEnLista }
        }
    }

    fun eliminarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual -> carritoActual.filterNot { it.id == item.id } }
    }

    fun agregarNuevoProducto(nombre: String, descripcion: String, precio: Double, stock: Int, categoria: String, imagenUrl: String) {
        _productos.update { listaActual ->
            val nuevoId = (listaActual.maxOfOrNull { it.id } ?: 0) + 1
            val nuevoProducto = Producto(
                id = nuevoId, 
                nombre = nombre, 
                descripcion = descripcion, 
                precio = precio, 
                stock = stock, 
                categoria = categoria, 
                imagenUrl = imagenUrl.ifEmpty { null }, 
                imagenResId = if (imagenUrl.isEmpty()) R.drawable.capuchino else null
            )
            listaActual + nuevoProducto
        }
    }

    fun eliminarProducto(productoId: Int) {
        _productos.update { listaActual -> listaActual.filterNot { it.id == productoId } }
        _carrito.update { carritoActual -> carritoActual.filterNot { it.producto.id == productoId } }
    }

    fun actualizarProducto(productoModificado: Producto) {
        _productos.update { listaActual ->
            listaActual.map { if (it.id == productoModificado.id) productoModificado else it }
        }
    }

    fun getProductoPorId(productoId: Int): Producto? {
        return _productos.value.find { it.id == productoId }
    }

    fun generarBoleta(userEmail: String): String {
        val itemsComprados = _carrito.value
        val totalPagado = totalCarrito.value
        val nuevaBoleta = Boleta(
            id = UUID.randomUUID().toString().substring(0, 8),
            items = itemsComprados,
            total = totalPagado,
            fecha = System.currentTimeMillis(),
            userEmail = userEmail
        )
        _boletas.update { registroActual -> registroActual + nuevaBoleta }
        _carrito.value = emptyList()
        return nuevaBoleta.id
    }
}