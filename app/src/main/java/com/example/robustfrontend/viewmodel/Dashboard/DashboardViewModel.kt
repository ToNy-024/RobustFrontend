package com.example.robustfrontend.viewmodel.Dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.robustfrontend.R
import com.example.robustfrontend.data.model.ActividadUsuario
import com.example.robustfrontend.data.network.RetrofitInstance
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// Data class para empaquetar los datos del gráfico
data class ChartUiData(val entries: List<BarEntry>, val labels: List<String>)

class DashboardViewModel : ViewModel() {

    private val _chartData = MutableLiveData<ChartUiData>()
    val chartData: LiveData<ChartUiData> = _chartData

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _navigateToGroup = MutableLiveData<Boolean>()
    val navigateToGroup: LiveData<Boolean> = _navigateToGroup

    private val _navigateToAdmin = MutableLiveData<Boolean>()
    val navigateToAdmin: LiveData<Boolean> = _navigateToAdmin

    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> = _isAdmin

    fun checkUserAdminStatus(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getUsuario(userId)
                if (response.isSuccessful) {
                    _isAdmin.value = response.body()?.esAdmin == true
                }
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun fetchUserActivityScores(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getActividadesPorUsuario(userId)
                if (response.isSuccessful && response.body() != null) {
                    processActivitiesForChart(response.body()!!)
                } else {
                    _toastMessage.value = R.string.dashboard_load_data_error
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error al obtener datos", e)
                _toastMessage.value = R.string.error_connection
            }
        }
    }

    private fun processActivitiesForChart(activities: List<ActividadUsuario>) {
        val dateParser = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
        val dayKeyFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())

        val scoresByDay = activities
            .mapNotNull { activity ->
                try {
                    val date = dateParser.parse(activity.fechaCompletada)
                    val dayKey = dayKeyFormatter.format(date!!)
                    dayKey to activity.puntajeObtenido
                } catch (e: Exception) {
                    null
                }
            }
            .groupBy { it.first }
            .mapValues { entry -> entry.value.sumOf { it.second } }
            .toSortedMap()

        val labels = scoresByDay.keys.map { dateString ->
            try {
                val date = dayKeyFormatter.parse(dateString)
                displayFormatter.format(date!!)
            } catch (e: Exception) {
                ""
            }
        }

        val entries = scoresByDay.values.mapIndexed {
            index, score -> BarEntry(index.toFloat(), score.toFloat())
        }
        
        _chartData.value = ChartUiData(entries, labels)
    }
    
    fun onGroupNavigationSelected() {
        _navigateToGroup.value = true
    }

    fun onAdminNavigationSelected() {
        _navigateToAdmin.value = true
    }

    fun onNavigationComplete() {
        _navigateToGroup.value = false
        _navigateToAdmin.value = false
    }
}
