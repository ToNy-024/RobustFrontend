package com.example.robustfrontend.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.robustfrontend.databinding.ActivityDashboardBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mantenemos tu configuración para la UI de borde a borde

        // Configurar ViewBinding
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajustar paddings para las barras del sistema (como tenías)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        // Personalizar el saludo
        binding.textViewWelcome.text = "¡Hola, ${currentUser?.displayName?.split(" ")?.get(0) ?: "Usuario"}!"

        // Configurar el gráfico
        setupBarChart()
        loadChartData()

        // Configurar los listeners de los botones
        setupListeners()
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawValueAboveBar(true)
            setDrawGridBackground(false)
            // Aquí puedes añadir más configuraciones de estilo para el gráfico
            xAxis.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
        }
    }

    private fun loadChartData() {
        // --- DATOS DE EJEMPLO ---
        // TODO: Reemplazar esto con una llamada a tu ViewModel/API para obtener los datos reales.
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 120f)) // Puntaje Día 1
        entries.add(BarEntry(2f, 80f))  // Puntaje Día 2
        entries.add(BarEntry(3f, 150f)) // Puntaje Día 3
        entries.add(BarEntry(4f, 95f))  // ... y así sucesivamente

        val dataSet = BarDataSet(entries, "Puntaje Diario")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.invalidate() // Refrescar el gráfico
    }

    private fun setupListeners() {
        binding.cardViewGroup.setOnClickListener {
            // TODO: Navegar a la actividad del grupo (GroupActivity)
            Toast.makeText(this, "Abriendo grupo...", Toast.LENGTH_SHORT).show()
        }

        binding.buttonCreateGroup.setOnClickListener {
            // TODO: Navegar a la actividad para crear un grupo
            Toast.makeText(this, "Abriendo creación de grupo...", Toast.LENGTH_SHORT).show()
        }

        binding.buttonJoinGroup.setOnClickListener {
            // TODO: Mostrar un diálogo para introducir el código de invitación
            Toast.makeText(this, "Abriendo diálogo para unirse...", Toast.LENGTH_SHORT).show()
        }
    }
}
