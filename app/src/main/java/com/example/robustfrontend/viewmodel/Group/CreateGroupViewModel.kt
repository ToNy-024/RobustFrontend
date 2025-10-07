package com.example.robustfrontend.viewmodel.Group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.data.model.Grupo
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class CreateGroupViewModel : ViewModel() {

    // LiveData para estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para el grupo que se está editando
    private val _grupo = MutableLiveData<Grupo>()
    val grupo: LiveData<Grupo> = _grupo

    // LiveData para comunicar mensajes
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // LiveData para señalar que la operación terminó y se puede cerrar la actividad
    private val _operationComplete = MutableLiveData<Boolean>()
    val operationComplete: LiveData<Boolean> = _operationComplete

    fun loadGroupData(groupId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getGrupo(groupId)
                if (response.isSuccessful) {
                    _grupo.value = response.body()
                } else {
                    _toastMessage.value = "Error al cargar datos del grupo: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("CreateGroupVM", "Error al cargar grupo", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createGroup(name: String, description: String, creatorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // El backend debería generar el código de invitación y la fecha
                val newGroup = Grupo(
                    idGru = 0, // El backend lo ignora y asigna uno nuevo
                    nombre = name,
                    descripcion = description,
                    creador = creatorId,
                    fechaCreacion = "", // El backend debería gestionarlo
                    codigoInvitacion = "", // El backend debería generarlo
                    imagen = null
                )
                val response = RetrofitInstance.api.createGrupo(newGroup)
                if (response.isSuccessful) {
                    _toastMessage.value = "Grupo creado con éxito"
                    _operationComplete.value = true
                } else {
                    _toastMessage.value = "Error al crear el grupo: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("CreateGroupVM", "Error al crear grupo", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateGroup(groupId: Int, name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dataToUpdate = mapOf(
                    "nombre" to name,
                    "descripcion" to description
                )
                val response = RetrofitInstance.api.updateGrupo(groupId, dataToUpdate)
                if (response.isSuccessful) {
                    _toastMessage.value = "Grupo actualizado con éxito"
                    _operationComplete.value = true
                } else {
                    _toastMessage.value = "Error al actualizar el grupo: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("CreateGroupVM", "Error al actualizar grupo", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onOperationCompleteHandled() {
        _operationComplete.value = false
    }
}
