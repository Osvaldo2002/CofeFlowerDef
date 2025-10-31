package com.example.aplicacion

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin = _esAdmin.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail = _userEmail.asStateFlow()


    fun login(email: String, password: String): Boolean {

        if (email.equals("admin@coffeeflower.com", ignoreCase = true) && password == "admin123") {
            _esAdmin.value = true
            _userEmail.value = email
            return true
        }

        if (email.equals("cliente@gmail.com", ignoreCase = true) && password == "cliente123") {
            _esAdmin.value = false
            _userEmail.value = email
            return true
        }

        _esAdmin.value = false
        _userEmail.value = null
        return false
    }

    fun logout() {
        _esAdmin.value = false
        _userEmail.value = null
    }
}