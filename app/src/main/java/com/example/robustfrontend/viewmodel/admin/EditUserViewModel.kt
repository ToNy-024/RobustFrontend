package com.example.robustfrontend.viewmodel.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Usuario
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de edición de usuario. Gestiona la carga de los datos de un usuario
 * y la actualización de su estado de administrador.
 */
class EditUserViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _user = MutableLiveData<Usuario>()
    val user: LiveData<Usuario> = _user

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    /**
     * Carga los datos de un usuario específico desde el backend.
     * @param userId El ID del usuario a cargar.
     */
    fun fetchUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getUsuario(userId)
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _toastMessage.value = R.string.group_load_user_data_error // Reutilizamos string
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza el estado de administrador de un usuario.
     * @param userId El ID del usuario a modificar.
     * @param isAdmin El nuevo estado de administrador (true o false).
     */
    fun updateUserAdminStatus(userId: String, isAdmin: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.updateUsuario(userId, mapOf("esAdmin" to isAdmin))
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.update_group_success // Reutilizamos string
                    _updateSuccess.value = true
                } else {
                    _toastMessage.value = R.string.update_group_error // Reutilizamos string
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Reinicia el estado de la bandera que indica que la actualización fue exitosa.
     */
    fun onUpdateComplete() {
        _updateSuccess.value = false
    }
}
