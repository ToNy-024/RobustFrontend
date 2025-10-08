package com.example.robustfrontend.viewmodel.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.data.model.Usuario
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class AdminUserViewModel : ViewModel() {

    private val _users = MutableLiveData<List<Usuario>>()
    val users: LiveData<List<Usuario>> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private var allUsers: List<Usuario> = listOf()

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getUsuarios()
                if (response.isSuccessful) {
                    allUsers = response.body() ?: emptyList()
                    _users.value = allUsers
                } else {
                    _toastMessage.value = "Error al cargar los usuarios: ${response.message()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchUsers(query: String?) {
        if (query.isNullOrBlank()) {
            _users.value = allUsers
        } else {
            val filteredList = allUsers.filter { user ->
                user.nombre.contains(query, ignoreCase = true)
            }
            _users.value = filteredList
        }
    }
}
