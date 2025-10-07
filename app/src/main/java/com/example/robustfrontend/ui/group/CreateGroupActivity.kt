package com.example.robustfrontend.ui.group

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.robustfrontend.databinding.ActivityCreateGroupBinding
import com.example.robustfrontend.viewmodel.Group.CreateGroupViewModel
import com.google.firebase.auth.FirebaseAuth

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    // ¡TODO COMPLETADO! Se instancia el ViewModel
    private val viewModel: CreateGroupViewModel by viewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var isEditMode = false
    private var groupId: Int? = null

    companion object {
        const val EXTRA_GROUP_ID = "GROUP_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent.hasExtra(EXTRA_GROUP_ID)) {
            isEditMode = true
            groupId = intent.getIntExtra(EXTRA_GROUP_ID, -1)
        }

        setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupUI() {
        if (isEditMode) {
            binding.textViewTitle.text = "Editar Grupo"
            binding.buttonSaveGroup.text = "Guardar Cambios"
            // ¡TODO COMPLETADO! Cargar datos del grupo existente
            groupId?.let {
                if (it != -1) {
                    viewModel.loadGroupData(it)
                }
            }
        } else {
            binding.textViewTitle.text = "Crear Nuevo Grupo"
            binding.buttonSaveGroup.text = "Crear Grupo"
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })

        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })

        // ¡TODO COMPLETADO! Observador para rellenar los campos en modo edición
        viewModel.grupo.observe(this, Observer { grupo ->
            binding.editTextGroupName.setText(grupo.nombre)
            binding.editTextGroupDescription.setText(grupo.descripcion)
        })

        // ¡TODO COMPLETADO! Observador para finalizar la actividad tras una operación exitosa
        viewModel.operationComplete.observe(this, Observer { isComplete ->
            if (isComplete) {
                // Finaliza la actividad y vuelve a la anterior
                finish()
                viewModel.onOperationCompleteHandled() // Resetea el evento
            }
        })
    }

    private fun setupListeners() {
        binding.buttonSaveGroup.setOnClickListener {
            val name = binding.editTextGroupName.text.toString().trim()
            val description = binding.editTextGroupDescription.text.toString().trim()
            val userId = firebaseAuth.currentUser?.uid

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "El nombre y la descripción no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == null) {
                Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                // ¡TODO COMPLETADO! Llamada para actualizar
                groupId?.let { viewModel.updateGroup(it, name, description) }
            } else {
                // ¡TODO COMPLETADO! Llamada para crear
                viewModel.createGroup(name, description, userId)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSaveGroup.isEnabled = !isLoading
    }
}
