package com.example.robustfrontend.viewmodel.Activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.data.model.Actividad
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    // LiveData para el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para comunicar mensajes a la UI (e.g., errores, éxito)
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // LiveData para indicar que la operación de creación fue exitosa y se puede cerrar la vista
    private val _creationSuccess = MutableLiveData<Boolean>()
    val creationSuccess: LiveData<Boolean> = _creationSuccess

    /**
     * Propone una nueva actividad para su votación.
     * El backend debe crearla con estado "pendiente" y disparar las notificaciones.
     */
    fun proposeActivity(
        name: String,
        description: String,
        frecuencia: String,
        dificultad: Int,
        desagradable: Int,
        groupId: Int,
        creatorId: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Creamos el objeto Actividad con todos los datos del formulario
                val newActivity = Actividad(
                    idAct = 0, // El backend debe asignar el ID
                    nombre = name,
                    descripcion = description,
                    frecuencia = frecuencia,
                    dificultad = dificultad,
                    desagradable = desagradable,
                    puntaje = 100,        // El backend podría calcularlo o usar un valor base
                    idGru = groupId,
                    fCreacion = "",       // El backend debe gestionar la fecha
                    creador = creatorId,
                    estado = "pendiente"  // Estado inicial para la votación
                )

                // Llamamos a la API para crear la actividad
                val response = RetrofitInstance.api.createActividad(newActivity)

                if (response.isSuccessful) {
                    _toastMessage.value = response.body()?.get("message") ?: "Actividad propuesta con éxito."
                    _creationSuccess.value = true
                } else {
                    _toastMessage.value = "Error al proponer la actividad: ${response.errorBody()?.string()}"
                    _creationSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("ActivityViewModel", "Error al proponer actividad", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
                _creationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Resetea el estado del evento de éxito para evitar que se dispare de nuevo
     * en cambios de configuración (como rotar la pantalla).
     */
    fun onCreationSuccessHandled() {
        _creationSuccess.value = false
    }
}
