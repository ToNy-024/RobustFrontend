package com.example.robustfrontend.viewmodel.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.Actividad
import com.example.robustfrontend.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class CreateActivityViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _operationComplete = MutableLiveData<Boolean>()
    val operationComplete: LiveData<Boolean> = _operationComplete

    private val _activity = MutableLiveData<Actividad>()
    val activity: LiveData<Actividad> = _activity

    fun loadActivity(activityId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getActividad(activityId)
                if (response.isSuccessful) {
                    _activity.value = response.body()
                } else {
                    _toastMessage.value = R.string.load_activity_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createActivity(
        nombre: String, descripcion: String, creadorId: String, grupoId: Int, estado: String,
        frecuencia: String, dificultad: Int, desagradable: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newActivity = Actividad(0, nombre, descripcion, frecuencia, dificultad, desagradable, 100, grupoId, "", creadorId, estado)
                val response = RetrofitInstance.api.createActividad(newActivity)
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.propose_activity_success
                    _operationComplete.value = true
                } else {
                    _toastMessage.value = R.string.propose_activity_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateActivity(
        activityId: Int, nombre: String, descripcion: String, frecuencia: String, 
        dificultad: Int, desagradable: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val dataToUpdate = mapOf(
                "nombre" to nombre,
                "descripcion" to descripcion,
                "frecuencia" to frecuencia,
                "dificultad" to dificultad,
                "desagradable" to desagradable
            )
            try {
                val response = RetrofitInstance.api.updateActividad(activityId, dataToUpdate)
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.update_activity_success
                    _operationComplete.value = true
                } else {
                    _toastMessage.value = R.string.update_activity_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteActivity(activityId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.deleteActividad(activityId)
                if (response.isSuccessful) {
                    _toastMessage.value = R.string.delete_activity_success
                    _operationComplete.value = true
                } else {
                    _toastMessage.value = R.string.delete_activity_error
                }
            } catch (e: Exception) {
                _toastMessage.value = R.string.error_connection
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onOperationCompleteHandled() {
        _operationComplete.value = false
    }
}
