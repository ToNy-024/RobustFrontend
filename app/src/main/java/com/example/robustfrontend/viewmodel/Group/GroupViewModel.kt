package com.example.robustfrontend.viewmodel.Group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.data.model.Grupo
import com.example.robustfrontend.data.model.Usuario
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {

    // LiveData para el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para el usuario y su grupo
    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    private val _grupo = MutableLiveData<Grupo?>()
    val grupo: LiveData<Grupo?> = _grupo

    // LiveData para comunicar mensajes a la UI (e.g., errores)
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // LiveData para indicar que la operación de unirse fue exitosa
    private val _joinSuccess = MutableLiveData<Boolean>()
    val joinSuccess: LiveData<Boolean> = _joinSuccess


    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getUsuario(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    _usuario.value = user
                    if (user?.idGru != null) {
                        // Si el usuario tiene un ID de grupo, cargamos los datos del grupo
                        loadGroupData(user.idGru)
                    } else {
                        // Si no tiene grupo, limpiamos el LiveData del grupo y terminamos la carga
                        _grupo.value = null
                        _isLoading.value = false
                    }
                } else {
                    _toastMessage.value = "Error al obtener datos del usuario: ${response.message()}"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error al cargar usuario", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun loadGroupData(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGrupo(groupId)
                if (response.isSuccessful) {
                    _grupo.value = response.body()
                } else {
                    _toastMessage.value = "Error al obtener datos del grupo: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error al cargar grupo", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false // Terminamos la carga aquí, después de obtener el grupo
            }
        }
    }

    fun joinGroup(userId: String, invitationCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // El backend se encargará de encontrar el grupo por código y asociar al usuario
                val response = RetrofitInstance.api.updateUsuario(userId, mapOf("codigo_invitacion" to invitationCode))
                if (response.isSuccessful) {
                    _toastMessage.value = "¡Te has unido al grupo con éxito!"
                    _joinSuccess.value = true // Indica éxito para que la UI se refresque
                } else {
                    // El backend debería devolver un error claro, como "Código inválido"
                    _toastMessage.value = "No se pudo unir al grupo. Código inválido o error del servidor."
                    _joinSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error al unirse al grupo", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
                _joinSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onJoinSuccessComplete() {
        _joinSuccess.value = false
    }
}
