package com.example.robustfrontend.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.robustfrontend.R
import com.example.robustfrontend.databinding.ActivityCreateActivityBinding
import com.example.robustfrontend.viewmodel.activity.CreateActivityViewModel
import com.google.firebase.auth.FirebaseAuth

class CreateActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateActivityBinding
    private val viewModel: CreateActivityViewModel by viewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private var isEditMode = false
    private var groupId: Int = -1
    private var activityId: Int = -1
    private var isAdmin: Boolean = false

    companion object {
        const val EXTRA_GROUP_ID = "GROUP_ID"
        const val EXTRA_ACTIVITY_ID = "ACTIVITY_ID"
        const val EXTRA_IS_ADMIN = "IS_ADMIN"
    }

    /**
     * Punto de entrada de la actividad. Se ejecuta al crear la vista.
     * Inicializa la vinculación de vistas, determina si la pantalla está en modo de creación o edición,
     * valida los datos de entrada y llama a las funciones de configuración.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(EXTRA_ACTIVITY_ID)) {
            isEditMode = true
            activityId = intent.getIntExtra(EXTRA_ACTIVITY_ID, -1)
        } else {
            groupId = intent.getIntExtra(EXTRA_GROUP_ID, -1)
            isAdmin = intent.getBooleanExtra(EXTRA_IS_ADMIN, false)
        }

        if ((isEditMode && activityId == -1) || (!isEditMode && groupId == -1)) {
            Toast.makeText(this, getString(R.string.activity_invalid_id_error), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI()
        setupSpinner()
        setupListeners()
        setupObservers()

        if (isEditMode) {
            viewModel.loadActivity(activityId)
        }
    }

    /**
     * Configura los elementos de la interfaz de usuario (títulos y textos de botones)
     * según si la actividad está en modo de creación o edición.
     */
    private fun setupUI() {
        if (isEditMode) {
            binding.textViewTitle.text = getString(R.string.edit_activity_title)
            binding.buttonProposeActivity.text = getString(R.string.save_changes)
        } else {
            binding.textViewTitle.text = getString(R.string.propose_activity_title)
            binding.buttonProposeActivity.text = getString(R.string.propose_activity_button)
        }
    }

    /**
     * Inicializa el Spinner de frecuencia, cargando las opciones desde los recursos
     * de strings y configurando su adaptador.
     */
    private fun setupSpinner() {
        val frequencies = resources.getStringArray(R.array.activity_frequencies_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrequency.adapter = adapter
    }

    /**
     * Configura los observadores de LiveData para reaccionar a los cambios del ViewModel.
     * Gestiona el estado de carga, la visualización de mensajes (Toast), el fin de la actividad
     * tras una operación exitosa y el llenado de datos en modo edición.
     */
    private fun setupObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading -> showLoading(isLoading) })

        viewModel.toastMessage.observe(this, Observer { messageResId ->
            Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
        })

        viewModel.operationComplete.observe(this, Observer { isComplete ->
            if (isComplete) {
                finish()
                viewModel.onOperationCompleteHandled()
            }
        })

        viewModel.activity.observe(this, Observer { activity ->
            binding.editTextActivityName.setText(activity.nombre)
            binding.editTextActivityDescription.setText(activity.descripcion)
            binding.sliderDifficulty.value = activity.dificultad.toFloat()
            binding.sliderUnpleasantness.value = activity.desagradable.toFloat()

            val frequencies = resources.getStringArray(R.array.activity_frequencies_array).map { it.lowercase() }
            val position = frequencies.indexOf(activity.frecuencia.lowercase())
            if (position >= 0) {
                binding.spinnerFrequency.setSelection(position)
            }
        })
    }

    /**
     * Configura el listener del botón principal. Recoge los datos de la interfaz,
     * valida que no estén vacíos y llama a la función correspondiente del ViewModel
     * (`createActivity` o `updateActivity`) según el modo de la pantalla.
     */
    private fun setupListeners() {
        binding.buttonProposeActivity.setOnClickListener {
            val nombre = binding.editTextActivityName.text.toString().trim()
            val descripcion = binding.editTextActivityDescription.text.toString().trim()

            if (nombre.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, getString(R.string.create_group_empty_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val frecuencia = binding.spinnerFrequency.selectedItem.toString().lowercase()
            val dificultad = binding.sliderDifficulty.value.toInt()
            val desagradable = binding.sliderUnpleasantness.value.toInt()

            if (isEditMode) {
                viewModel.updateActivity(activityId, nombre, descripcion, frecuencia, dificultad, desagradable)
            } else {
                val creadorId = firebaseAuth.currentUser?.uid
                if (creadorId == null) {
                    Toast.makeText(this, getString(R.string.error_unauthorized_user), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val estado = if (isAdmin) "aprobada" else "pendiente"
                viewModel.createActivity(nombre, descripcion, creadorId, groupId, estado, frecuencia, dificultad, desagradable)
            }
        }
    }

    /**
     * Muestra u oculta la barra de progreso y habilita o deshabilita el botón principal
     * para dar feedback visual durante las operaciones de red.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonProposeActivity.isEnabled = !isLoading
    }
}
