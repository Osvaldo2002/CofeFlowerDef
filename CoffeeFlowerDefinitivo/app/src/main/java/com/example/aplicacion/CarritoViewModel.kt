package com.example.aplicacion

import com.example.aplicacion.model.Boleta
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
import java.util.UUID
import kotlin.collections.filterNot

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

    // --- 👇 MODIFICACIÓN AQUÍ (Nuevos productos añadidos) 👇 ---
    private fun cargarProductos() {
        val opcionLeche = OpcionProducto(
            "Tipo de Leche", listOf(
                ValorOpcion("Leche Entera", 0.0),
                ValorOpcion("Leche Sin Lactosa", 200.0),
                ValorOpcion("Leche de Almendras", 300.0)
            )
        )

        _productos.value = listOf(
            // Productos de Café (existentes)
            Producto(
                id = 1, nombre = "Capuchino",
                descripcion = "Café espresso con leche vaporizada y espuma.",
                precio = 3200.0, stock = 100, categoria = "Cafés",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Cappuccino_at_Sightglass_Coffee.jpg/1280px-Cappuccino_at_Sightglass_Coffee.jpg",
                opciones = listOf(opcionLeche)
            ),
            Producto(
                id = 2, nombre = "Mokachino",
                descripcion = "Capuchino con un toque de chocolate.",
                precio = 3500.0, stock = 100, categoria = "Cafés",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Mocaccino-Coffee.jpg/1024px-Mocaccino-Coffee.jpg",
                opciones = listOf(opcionLeche)
            ),
            Producto(
                id = 3, nombre = "Latte",
                descripcion = "Café espresso con más leche vaporizada que espuma.",
                precio = 3000.0, stock = 100, categoria = "Cafés",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d8/Coffee_sydney.jpg/1280px-Coffee_sydney.jpg",
                opciones = listOf(opcionLeche)
            ),
            Producto(
                id = 4, nombre = "Americano",
                descripcion = "Café espresso diluido con agua caliente.",
                precio = 2200.0, stock = 100, categoria = "Cafés",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Hokitika_Cheese_and_Deli%2C_Hokitika_%283526706594%29.jpg/1280px-Hokitika_Cheese_and_Deli%2C_Hokitika_%283526706594%29.jpg",
                opciones = emptyList()
            ),
            Producto(
                id = 5, nombre = "Expresso",
                descripcion = "Café puro y concentrado.",
                precio = 2000.0, stock = 100, categoria = "Cafés",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/A_small_cup_of_coffee.JPG/1280px-A_small_cup_of_coffee.JPG",
                opciones = emptyList()
            ),
            Producto(
                id = 6, nombre = "Cortado",
                descripcion = "Café espresso con una pequeña cantidad de leche.",
                precio = 2300.0, stock = 100, categoria = "Cafés",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Caf%C3%A9_cortado_leche_y_leche.jpg/1280px-Caf%C3%A9_cortado_leche_y_leche.jpg",
                opciones = listOf(opcionLeche)
            ),

            // --- 👇 PRODUCTOS NUEVOS AÑADIDOS AQUÍ 👇 ---
            Producto(
                id = 7, nombre = "Pastel de Chocolate",
                descripcion = "Una rebanada de pastel de chocolate húmedo.",
                precio = 3800.0, stock = 50, categoria = "Pastelería",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Chocolate_cake_with_chocolate_frosting_and_chocolate_sprinkles.jpg/1280px-Chocolate_cake_with_chocolate_frosting_and_chocolate_sprinkles.jpg",
                opciones = emptyList()
            ),
            Producto(
                id = 8, nombre = "Té Matcha Latte",
                descripcion = "Té verde matcha ceremonial con leche vaporizada.",
                precio = 3900.0, stock = 80, categoria = "Té e Infusiones",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Matcha_Tee_Gr%C3%BCner_Tee_matcha_Latte.jpg/1280px-Matcha_Tee_Gr%C3%BCner_Tee_matcha_Latte.jpg",
                opciones = listOf(opcionLeche) // El Matcha puede usar la opción de leche
            ),
            Producto(
                id = 9, nombre = "Té Helado de Frambuesa",
                descripcion = "Té negro helado refrescante con un toque de frambuesa.",
                precio = 2800.0, stock = 100, categoria = "Té e Infusiones",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/Raspberry_Iced_Tea.jpg/1280px-Raspberry_Iced_Tea.jpg",
                opciones = emptyList()
            ),
            Producto(
                id = 10, nombre = "Galleta con Chips de Chocolate",
                descripcion = "Galleta grande y suave con chips de chocolate.",
                precio = 1800.0, stock = 150, categoria = "Dulces",
                imagenUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Chocolate_Chip_Cookies_-_kimber-.-.jpg/1280px-Chocolate_Chip_Cookies_-_kimber-.-.jpg",
                opciones = emptyList()
            )
            // --- 👆 FIN PRODUCTOS NUEVOS 👆 ---
        )
    }

    // --- Funciones del Carrito (Inmutables - Sin cambios) ---
    fun agregarAlCarrito(producto: Producto, opciones: Map<String, ValorOpcion>) {
        _carrito.update { carritoActual ->
            val itemExistente = carritoActual.find { it.producto.id == producto.id && it.opcionesSeleccionadas == opciones }

            if (itemExistente != null) {
                carritoActual.map {
                    if (it.id == itemExistente.id) {
                        it.copy(cantidad = it.cantidad + 1)
                    } else {
                        it
                    }
                }
            } else {
                carritoActual + CartItem(producto = producto, cantidad = 1, opcionesSeleccionadas = opciones)
            }
        }
    }

    fun restarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual ->
            carritoActual.mapNotNull { itemEnLista ->
                if (itemEnLista.id == item.id) {
                    if (itemEnLista.cantidad > 1) {
                        itemEnLista.copy(cantidad = itemEnLista.cantidad - 1)
                    } else {
                        null
                    }
                } else {
                    itemEnLista
                }
            }
        }
    }

    fun sumarAlCarrito(item: CartItem) {
        _carrito.update { carritoActual ->
            carritoActual.map { itemEnLista ->
                if (itemEnLista.id == item.id) {
                    itemEnLista.copy(cantidad = itemEnLista.cantidad + 1)
                } else {
                    itemEnLista
                }
            }
        }
    }

    fun eliminarDelCarrito(item: CartItem) {
        _carrito.update { carritoActual -> carritoActual.filterNot { it.id == item.id } }
    }

    // --- Funciones de Admin (Sin cambios) ---
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

    // --- Función de Pago (Sin cambios) ---
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
