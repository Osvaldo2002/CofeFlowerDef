package com.example.aplicacion

// --- Imports de tu modelo (del paquete 'model') ---
import com.example.aplicacion.model.CartItem
import com.example.aplicacion.model.Producto
import com.example.aplicacion.model.OpcionProducto
import com.example.aplicacion.model.ValorOpcion

// --- Imports de ViewModel ---
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// --- Imports de Flow (para el estado) ---
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

// --- Imports de utilidades de Kotlin ---
import kotlin.collections.filterNot

// ... aquí comienza tu "class CarritoViewModel : ViewModel() { ..."

class CarritoViewModel : ViewModel() {

    // --- Lista de Productos ---
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    // --- Carrito de Compras ---
    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()

    // --- Total del Carrito (Calculado automáticamente) ---
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
        // --- Definimos nuestras opciones ---
        val opcionLeche = OpcionProducto(
            nombre = "Tipo de Leche",
            valores = listOf(
                ValorOpcion("Leche Entera", 0.0),
                ValorOpcion("Leche Sin Lactosa", 200.0),
                ValorOpcion("Leche de Almendras", 300.0)
            )
        )

        // --- Tus 6 cafés (Versión corregida con todos los campos) ---
        _productos.value = listOf(
            Producto(
                id = 1,
                nombre = "Capuchino",
                descripcion = "Café espresso con leche vaporizada y espuma.",
                precio = 3200.0,
                stock = 100,
                categoria = "Cafés",
                imagenUrl = "",
                opciones = listOf(opcionLeche)
            ),
            Producto(
                id = 2,
                nombre = "Mokachino",
                descripcion = "Capuchino con un toque de chocolate.",
                precio = 3500.0,
                stock = 100,
                categoria = "Cafés",
                imagenUrl = "",
                opciones = listOf(opcionLeche)
            ),
            Producto(
                id = 3,
                nombre = "Latte",
                descripcion = "Café espresso con más leche vaporizada que espuma.",
                precio = 3000.0,
                stock = 100,
                categoria = "Cafés",
                imagenUrl = "",
                opciones = listOf(opcionLeche)
            ),
            Producto(
                id = 4,
                nombre = "Americano",
                descripcion = "Café espresso diluido con agua caliente.",
                precio = 2200.0,
                stock = 100,
                categoria = "Cafés",
                imagenUrl = ""
            ),
            Producto(
                id = 5,
                nombre = "Expresso",
                descripcion = "Café puro y concentrado.",
                precio = 2000.0,
                stock = 100,
                categoria = "Cafés",
                imagenUrl = ""
            ),
            Producto(
                id = 6,
                nombre = "Cortado",
                descripcion = "Café espresso con una pequeña cantidad de leche.",
                precio = 2300.0,
                stock = 100,
                categoria = "Cafés",
                imagenUrl = "",
                opciones = listOf(opcionLeche)
            )
        )
    }

    // --- Funciones para manipular el carrito ---

    fun agregarAlCarrito(producto: Producto, opciones: Map<String, ValorOpcion>) {
        _carrito.update { carritoActual ->
            val mutableCarrito = carritoActual.toMutableList()
            val itemExistente = mutableCarrito.find {
                it.producto.id == producto.id && it.opcionesSeleccionadas == opciones
            }

            if (itemExistente != null) {
                itemExistente.cantidad++
            } else {
                mutableCarrito.add(
                    CartItem(
                        producto = producto,
                        cantidad = 1,
                        opcionesSeleccionadas = opciones
                    )
                )
            }
            mutableCarrito
        }
    }

    fun restarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual ->
            val mutableCarrito = carritoActual.toMutableList()
            val itemEnLista = mutableCarrito.find { it.id == item.id }

            if (itemEnLista != null) {
                if (itemEnLista.cantidad > 1) {
                    itemEnLista.cantidad--
                } else {
                    mutableCarrito.remove(itemEnLista)
                }
            }
            mutableCarrito
        }
    }

    fun eliminarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual ->
            carritoActual.filterNot { it.id == item.id }
        }
    }

    /**
     * Agrega un nuevo producto a la lista general de productos.
     */
    fun agregarNuevoProducto(
        nombre: String,
        descripcion: String,
        precio: Double,
        stock: Int,
        categoria: String,
        imagenUrl: String
    ) {
        _productos.update { listaActual ->
            val nuevoId = (listaActual.maxOfOrNull { it.id } ?: 0) + 1
            val nuevoProducto = Producto(
                id = nuevoId,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio,
                stock = stock,
                categoria = categoria,
                imagenUrl = imagenUrl,
                opciones = emptyList() // Los productos nuevos se crean sin opciones por defecto
            )
            listaActual + nuevoProducto
        }
    }
}