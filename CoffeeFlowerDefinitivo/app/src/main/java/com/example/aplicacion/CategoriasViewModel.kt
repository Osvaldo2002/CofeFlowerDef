package com.example.aplicacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// 1. Asegúrate de importar desde el paquete 'model'
import com.example.aplicacion.model.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriasViewModel : ViewModel() {

    // 2. Usar MutableStateFlow para el estado
    private val _categorias = MutableStateFlow(
        // Carga inicial de datos
        listOf(
            Categoria(1, "Cafés Calientes"),
            Categoria(2, "Pastelería"),
            Categoria(3, "Bebidas Frías"),
            Categoria(4, "Té e Infusiones")
        )
    )
    // 3. Exponerlo como un StateFlow inmutable
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    // --- FUNCIONES CRUD (Actualizadas para StateFlow) ---

    fun agregarCategoria(nombre: String) {
        if (nombre.isBlank()) return

        viewModelScope.launch {
            _loading.value = true
            // Obtenemos el ID máximo de la lista actual
            val nuevoId = (_categorias.value.maxOfOrNull { it.id } ?: 0) + 1
            val nuevaCategoria = Categoria(nuevoId, nombre)

            // 4. Actualizamos el Flow creando una nueva lista
            _categorias.update { listaActual ->
                listaActual + nuevaCategoria // Añade la nueva categoría a la lista
            }
            _loading.value = false
        }
    }

    fun editarCategoria(categoria: Categoria, nuevoNombre: String) {
        if (nuevoNombre.isBlank()) return

        viewModelScope.launch {
            _loading.value = true
            _categorias.update { listaActual ->
                listaActual.map {
                    if (it.id == categoria.id) {
                        it.copy(nombre = nuevoNombre) // Actualiza solo el item
                    } else {
                        it
                    }
                }
            }
            _loading.value = false
        }
    }

    fun eliminarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            _loading.value = true
            _categorias.update { listaActual ->
                listaActual.filterNot { it.id == categoria.id } // Remueve el item
            }
            _loading.value = false
        }
    }
}