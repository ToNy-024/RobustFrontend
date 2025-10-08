package com.example.robustfrontend.viewmodel.Group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Actividad
import com.example.robustfrontend.data.model.Grupo
import com.example.robustfrontend.data.model.Usuario
import com.example.robustfrontend.data.model.VotoActividad
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Grupo. Gestiona los datos del grupo del usuario,
 * las listas de actividades y las operaciones relacionadas como unirse a un grupo, votar o mover actividades.
 */
class GroupViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _grupo = MutableLiveData<Grupo?>()
    val grupo: LiveData<Grupo?> = _grupo

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    private val _joinSuccess = MutableLiveData<Boolean>()
    val joinSuccess: LiveData<Boolean> = _joinSuccess

    private val _groupDeleted = MutableLiveData<Boolean>()
    val groupDeleted: LiveData<Boolean> = _groupDeleted

    private val _pendingActivities = MutableLiveData<List<Actividad>>(emptyList())
    val pendingActivities: LiveData<List<Actividad>> = _pendingActivities

    private val _todoActivities = MutableLiveData<List<Actividad>>(emptyList())
    val todoActivities: LiveData<List<Actividad>> = _todoActivities

    private val _inProgressActivities = MutableLiveData<List<Actividad>>(emptyList())
    val inProgressActivities: LiveData<List<Actividad>> = _inProgressActivities

    private val _doneActivities = MutableLiveData<List<Actividad>>(emptyList())
    val doneActivities: LiveData<List<Actividad>> = _doneActivities

    /**
     * Carga los datos del usuario desde el backend y, si pertenece a un grupo,
     * carga también los datos y actividades del grupo.
     * @param userId ID del usuario a cargar.
     */
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getUsuario(userId)
                if (response.isSuccessful) {
                    val user = response.body()
                    _usuario.value = user
                    if (user?.idGru != null) {
                        loadGroupData(user.idGru)
                        loadGroupActivities(user.idGru)
                    } else {
                        _grupo.value = null
                    }
                } else {
                    _toastMessage.value = R.string.group_load_user_data_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga la información detallada de un grupo específico.
     * @param groupId ID del grupo a cargar.
     */
    private fun loadGroupData(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGrupo(groupId)
                if (response.isSuccessful) {
                    _grupo.value = response.body()
                } else {
                    _toastMessage.value = R.string.group_load_group_data_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            }
        }
    }

    /**
     * Carga la lista de actividades de un grupo y las distribuye en las listas correspondientes
     * según su estado (pendiente, aprobada, en_progreso, hecha).
     * @param groupId ID del grupo del que se cargarán las actividades.
     */
    private fun loadGroupActivities(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getActividadesPorGrupo(groupId)
                if (response.isSuccessful) {
                    val allActivities = response.body() ?: emptyList()
                    _pendingActivities.value = allActivities.filter { it.estado == "pendiente" }
                    _todoActivities.value = allActivities.filter { it.estado == "aprobada" }
                    _inProgressActivities.value = allActivities.filter { it.estado == "en_progreso" }
                    _doneActivities.value = allActivities.filter { it.estado == "hecha" }
                } else {
                    _toastMessage.value = R.string.group_load_activities_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            }
        }
    }

    /**
     * Permite a un usuario unirse a un grupo utilizando un código de invitación.
     * @param userId ID del usuario que se une.
     * @param invitationCode Código de invitación del grupo.
     */
    fun joinGroup(userId: String, invitationCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.updateUsuario(userId, mapOf("codigo_invitacion" to invitationCode))
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.group_join_success
                    _joinSuccess.value = true
                } else {
                    _toastMessage.value = R.string.group_join_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza el estado de una actividad (ej. de 'aprobada' a 'en_progreso').
     * @param activityId ID de la actividad a actualizar.
     * @param newStatus Nuevo estado de la actividad.
     */
    fun updateActivityStatus(activityId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateActividad(activityId, mapOf("estado" to newStatus))
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.group_activity_status_updated
                    _grupo.value?.idGru?.let { loadGroupActivities(it) } // Recargar para reflejar el cambio
                } else {
                    _toastMessage.value = R.string.group_activity_status_update_error
                    _grupo.value?.idGru?.let { loadGroupActivities(it) } // Recargar para revertir en la UI
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
                _grupo.value?.idGru?.let { loadGroupActivities(it) } // Recargar para revertir en la UI
            }
        }
    }

    /**
     * Registra el voto (a favor o en contra) de un usuario para una actividad pendiente.
     * @param activityId ID de la actividad por la que se vota.
     * @param userId ID del usuario que vota.
     * @param approve True si el voto es de aprobación, false si es de rechazo.
     */
    fun voteForActivity(activityId: Int, userId: String, approve: Boolean) {
        viewModelScope.launch {
            val voto = VotoActividad(0, activityId, userId, approve, "")
            try {
                val response = RetrofitInstance.api.votarActividad(voto)
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.group_vote_success
                    _grupo.value?.idGru?.let { loadGroupActivities(it) }
                } else {
                    _toastMessage.value = R.string.group_vote_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            }
        }
    }

    /**
     * Elimina un grupo del sistema.
     * @param groupId ID del grupo a eliminar.
     */
    fun deleteGroup(groupId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.deleteGrupo(groupId)
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.group_delete_success
                    _groupDeleted.value = true
                } else {
                    _toastMessage.value = R.string.group_delete_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Reinicia el estado de la bandera que indica que el grupo fue eliminado.
     */
    fun onGroupDeletedComplete() {
        _groupDeleted.value = false
    }

    /**
     * Reinicia el estado de la bandera que indica que el usuario se unió a un grupo con éxito.
     */
    fun onJoinSuccessComplete() {
        _joinSuccess.value = false
    }
}
