package com.example.robustfrontend.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.robustfrontend.databinding.ActivityDashboardBinding
import com.example.robustfrontend.viewmodel.Dashboard.DashboardViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth

// TODO: Crea estas nuevas actividades vacías para que el código compile
// import com.example.robustfrontend.ui.group.CreateGroupActivity
// import com.example.robustfrontend.ui.group.GroupActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    // Corrección 4: La forma de instanciarlo es `viewModels()` con 's' al final
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            // Si por alguna razón el usuario no está logueado, finalizar para evitar errores.
            finish()
            return
        }

        binding.textViewWelcome.text = "¡Hola, ${currentUser.displayName?.split(" ")?.get(0) ?: "Usuario"}!"

        setupBarChart()
        setupListeners()
        setupObservers()

        // Iniciar la carga de datos
        viewModel.fetchUserActivityScores(currentUser.uid)
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawValueAboveBar(true)
            setDrawGridBackground(false)
            xAxis.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            legend.isEnabled = false // Ocultamos la leyenda para un look más limpio
        }
    }

    private fun loadChartData(entries: List<BarEntry>) {
        if (entries.isEmpty()) {
            // Manejar el caso donde no hay datos, por ejemplo, mostrando un texto
            binding.barChart.clear()
            binding.barChart.invalidate() // Limpia y refresca el gráfico
            return
        }

        val dataSet = BarDataSet(entries, "Puntaje Diario")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.animateY(1000) // Añadimos una animación
        binding.barChart.invalidate()
    }

    private fun setupListeners() {
        binding.cardViewGroup.setOnClickListener {
            viewModel.onViewGroupClicked()
        }

        binding.buttonCreateGroup.setOnClickListener {
            viewModel.onCreateGroupClicked()
        }

        binding.buttonJoinGroup.setOnClickListener {
            showJoinGroupDialog()
        }
    }

    private fun setupObservers() {
        // Observador para los datos del gráfico
        viewModel.chartData.observe(this, Observer { entries ->
            loadChartData(entries)
        })

        // Observador para los mensajes Toast
        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })

        // Observadores para la navegación
        viewModel.navigateToGroup.observe(this, Observer { navigate ->
            if (navigate) {
                // TODO: Descomenta esto cuando crees la GroupActivity
                // val intent = Intent(this, GroupActivity::class.java)
                // startActivity(intent)
                Toast.makeText(this, "Navegando al grupo...", Toast.LENGTH_SHORT).show()
                viewModel.onNavigationComplete() // Resetea el evento
            }
        })

        viewModel.navigateToCreateGroup.observe(this, Observer { navigate ->
            if (navigate) {
                // TODO: Descomenta esto cuando crees la CreateGroupActivity
                // val intent = Intent(this, CreateGroupActivity::class.java)
                // startActivity(intent)
                Toast.makeText(this, "Navegando a crear grupo...", Toast.LENGTH_SHORT).show()
                viewModel.onNavigationComplete() // Resetea el evento
            }
        })
    }

    private fun showJoinGroupDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Unirse a un Grupo")

        val input = EditText(this)
        input.hint = "Introduce el código de invitación"
        builder.setView(input)

        builder.setPositiveButton("Unirse") { dialog, _ ->
            val code = input.text.toString().trim()
            if (code.isNotEmpty()) {
                // TODO: Implementar la lógica para unirse al grupo con la API
                // Por ejemplo: viewModel.joinGroup(code)
                Toast.makeText(this, "Uniéndote al grupo con código: $code", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "El código no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}
