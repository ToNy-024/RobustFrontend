package com.example.robustfrontend.viewmodel.Dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.data.model.ActividadUsuario
import com.example.robustfrontend.data.network.RetrofitInstance
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardViewModel : ViewModel() {

    // LiveData para los datos del gráfico
    private val _chartData = MutableLiveData<List<BarEntry>>()
    val chartData: LiveData<List<BarEntry>> = _chartData

    // LiveData para manejar errores o mensajes
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // LiveData para la navegación
    private val _navigateToGroup = MutableLiveData<Boolean>()
    val navigateToGroup: LiveData<Boolean> = _navigateToGroup

    private val _navigateToCreateGroup = MutableLiveData<Boolean>()
    val navigateToCreateGroup: LiveData<Boolean> = _navigateToCreateGroup

    // Función para obtener y procesar los datos de las actividades completadas por un usuario
    fun fetchUserActivityScores(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getActividadesPorUsuario(userId)
                if (response.isSuccessful && response.body() != null) {
                    val activities = response.body()!!
                    processActivitiesForChart(activities)
                } else {
                    _toastMessage.value = "Error al cargar los datos: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Excepción al obtener datos", e)
                _toastMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // Procesa la lista de actividades y las agrupa por día para el gráfico
    private fun processActivitiesForChart(activities: List<ActividadUsuario>) {
        val dateParser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
        val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val scoresByDay = activities
            .mapNotNull { activity ->
                try {
                    val date = dateParser.parse(activity.fechaCompletada)
                    val dayKey = dayFormatter.format(date!!)
                    dayKey to activity.puntajeObtenido
                } catch (e: Exception) {
                    null // Ignorar fechas con formato incorrecto
                }
            }
            .groupBy { it.first } // Agrupar por día (ej: "2023-10-27")
            .mapValues { entry -> entry.value.sumOf { it.second } } // Sumar los puntajes de cada día
            .toSortedMap() // Ordenar por fecha

        val chartEntries = scoresByDay.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        _chartData.value = chartEntries
    }

    // Funciones para manejar los eventos de clic
    fun onViewGroupClicked() {
        // Aquí podrías añadir lógica, como comprobar si el usuario realmente pertenece a un grupo
        _navigateToGroup.value = true
    }

    fun onCreateGroupClicked() {
        _navigateToCreateGroup.value = true
    }

    // Función para resetear el estado de navegación
    fun onNavigationComplete() {
        _navigateToGroup.value = false
        _navigateToCreateGroup.value = false
    }
}