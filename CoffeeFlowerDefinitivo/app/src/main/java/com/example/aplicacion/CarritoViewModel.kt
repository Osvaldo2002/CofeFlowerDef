package com.example.aplicacion

import com.example.aplicacion.model.Boleta // <-- NUEVO IMPORT
import com.example.aplicacion.model.CartItem
import com.example.aplicacion.model.Producto
import com.example.aplicacion.model.OpcionProducto
import com.example.aplicacion.model.ValorOpcion
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID // <-- NUEVO IMPORT
import kotlin.collections.filterNot

class CarritoViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    // --- NUEVO: Registro de Boletas ---
    private val _boletas = MutableStateFlow<List<Boleta>>(emptyList())
    val boletas: StateFlow<List<Boleta>> = _boletas.asStateFlow()
    // --- FIN NUEVO ---

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
        // ... (Tu función cargarProductos() queda exactamente igual) ...
        val opcionLeche = OpcionProducto("Tipo de Leche", listOf(ValorOpcion("Leche Entera", 0.0), ValorOpcion("Leche Sin Lactosa", 200.0), ValorOpcion("Leche de Almendras", 300.0)))
        _productos.value = listOf(
            Producto(1, "Capuchino", "Café espresso con leche vaporizada y espuma.", 3200.0, 100, "Cafés", "https://placehold.co/600x400/6F4E37/FFFFFF?text=Capuchino", listOf(opcionLeche)),
            Producto(2, "Mokachino", "Capuchino con un toque de chocolate.", 3500.0, 100, "Cafés", "https://placehold.co/600x400/6F4E37/FFFFFF?text=Mokachino", listOf(opcionLeche)),
            Producto(3, "Latte", "Café espresso con más leche vaporizada que espuma.", 3000.0, 100, "Cafés", "https://placehold.co/600x400/6F4E37/FFFFFF?text=Latte", listOf(opcionLeche)),
            Producto(4, "Americano", "Café espresso diluido con agua caliente.", 2200.0, 100, "Cafés", "https://placehold.co/600x400/6F4E37/FFFFFF?text=Americano"),
            Producto(5, "Expresso", "Café puro y concentrado.", 2000.0, 100, "Cafés", "https://placehold.co/600x400/6F4E37/FFFFFF?text=Expresso"),
            Producto(6, "Cortado", "Café espresso con una pequeña cantidad de leche.", 2300.0, 100, "Cafés", "https://placehold.co/600x400/6F4E37/FFFFFF?text=Cortado", listOf(opcionLeche))
        )
    }

    // --- Funciones del Carrito (Quedan igual) ---
    fun agregarAlCarrito(producto: Producto, opciones: Map<String, ValorOpcion>) {
        _carrito.update { carritoActual ->
            val mutableCarrito = carritoActual.toMutableList()
            val itemExistente = mutableCarrito.find { it.producto.id == producto.id && it.opcionesSeleccionadas == opciones }
            if (itemExistente != null) {
                itemExistente.cantidad++
            } else {
                mutableCarrito.add(CartItem(producto = producto, cantidad = 1, opcionesSeleccionadas = opciones))
            }
            mutableCarrito
        }
    }
    fun restarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual ->
            val mutableCarrito = carritoActual.toMutableList()
            val itemEnLista = mutableCarrito.find { it.id == item.id }
            if (itemEnLista != null) {
                if (itemEnLista.cantidad > 1) itemEnLista.cantidad-- else mutableCarrito.remove(itemEnLista)
            }
            mutableCarrito
        }
    }
    fun eliminarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual -> carritoActual.filterNot { it.id == item.id } }
    }

    // --- Funciones de Admin (Quedan igual) ---
    fun agregarNuevoProducto(nombre: String, descripcion: String, precio: Double, stock: Int, categoria: String, imagenUrl: String) {
        _productos.update { listaActual ->
            val nuevoId = (listaActual.maxOfOrNull { it.id } ?: 0) + 1
            val nuevoProducto = Producto(nuevoId, nombre, descripcion, precio, stock, categoria, imagenUrl, emptyList())
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

    // --- === NUEVA FUNCIÓN DE PAGO === ---
    /**
     * Genera una boleta, la guarda en el registro y limpia el carrito.
     * Devuelve el ID de la boleta generada.
     */
    fun generarBoleta(userEmail: String): String {
        val itemsComprados = _carrito.value
        val totalPagado = totalCarrito.value
        val nuevaBoleta = Boleta(
            id = UUID.randomUUID().toString().substring(0, 8), // ID corto
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