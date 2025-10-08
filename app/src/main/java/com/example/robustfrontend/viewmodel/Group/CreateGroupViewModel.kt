package com.example.robustfrontend.viewmodel.Group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Grupo
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de creación y edición de un grupo.
 * Gestiona la lógica para crear un nuevo grupo o actualizar uno existente.
 */
class CreateGroupViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _grupo = MutableLiveData<Grupo>()
    val grupo: LiveData<Grupo> = _grupo

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _operationComplete = MutableLiveData<Boolean>()
    val operationComplete: LiveData<Boolean> = _operationComplete

    /**
     * Carga los datos de un grupo existente para rellenar la pantalla en modo de edición.
     * @param groupId El ID del grupo a cargar.
     */
    fun loadGroupData(groupId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getGrupo(groupId)
                if (response.isSuccessful) {
                    _grupo.value = response.body()
                } else {
                    _toastMessage.value = R.string.load_group_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Crea un nuevo grupo en el backend con los datos proporcionados.
     * @param name Nombre del grupo.
     * @param description Descripción del grupo.
     * @param creatorId ID del usuario que crea el grupo.
     */
    fun createGroup(name: String, description: String, creatorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newGroup = Grupo(0, name, description, "", "", creatorId, null)
                val response = RetrofitInstance.api.createGrupo(newGroup)
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.create_group_success
                    _operationComplete.value = true
                } else {
                    _toastMessage.value = R.string.create_group_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza los datos de un grupo existente en el backend.
     * @param groupId ID del grupo a actualizar.
     * @param name Nuevo nombre para el grupo.
     * @param description Nueva descripción para el grupo.
     */
    fun updateGroup(groupId: Int, name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dataToUpdate = mapOf("nombre" to name, "descripcion" to description)
                val response = RetrofitInstance.api.updateGrupo(groupId, dataToUpdate)
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.update_group_success
                    _operationComplete.value = true
                } else {
                    _toastMessage.value = R.string.update_group_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Reinicia el estado de la bandera que indica que una operación (crear/actualizar) ha finalizado.
     */
    fun onOperationCompleteHandled() {
        _operationComplete.value = false
    }
}
