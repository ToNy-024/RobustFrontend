package com.example.robustfrontend.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Usuario
import com.example.robustfrontend.data.network.RetrofitInstance
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginComplete = MutableLiveData<Boolean>()
    val loginComplete: LiveData<Boolean> = _loginComplete

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    /**
     * Gestiona el inicio de sesión del usuario después de la autenticación de Firebase.
     * Comprueba si el usuario ya existe en el backend. Si no, lo crea.
     * @param user El objeto FirebaseUser del usuario que ha iniciado sesión.
     */
    fun handleUserLogin(user: FirebaseUser) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getUsuario(user.uid)
                if (response.isSuccessful) {
                    _loginComplete.value = true
                } else if (response.code() == 404) {
                    createNewUser(user)
                } else {
                    _toastMessage.value = R.string.login_verifying_user_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            }
        }
    }

    /**
     * Crea un nuevo usuario en el backend utilizando los datos de Firebase.
     * Si la creación es exitosa, el flujo de inicio de sesión se considera completo.
     * @param user El objeto FirebaseUser a partir del cual se crea el usuario.
     */
    private suspend fun createNewUser(user: FirebaseUser) {
        val newUser = Usuario(
            idUsu = user.uid,
            nombre = user.displayName ?: "User", // Se usará "User" si no hay nombre visible.
            puntajeMes = 0,
            puntajeTotal = 0,
            fechaRegistro = "", // El backend se encargará de la fecha.
            ultimaActividad = null,
            imagen = user.photoUrl?.toString(),
            idGru = null,
            esAdmin = false // Los nuevos usuarios no son administradores por defecto.
        )

        try {
            val createResponse = RetrofitInstance.api.createUsuario(newUser)
            if (createResponse.isSuccessful) {
                _loginComplete.value = true
            } else {
                _toastMessage.value = R.string.login_registering_user_error
            }
        } catch (e: Exception) {
            _toastMessage.value = R.string.login_connection_error_on_create
        }
    }
}
