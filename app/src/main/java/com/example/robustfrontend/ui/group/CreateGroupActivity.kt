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
import com.example.robustfrontend.R
import com.example.robustfrontend.databinding.ActivityCreateGroupBinding
import com.example.robustfrontend.viewmodel.Group.CreateGroupViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad para crear un nuevo grupo o editar uno existente.
 * El modo de operación (crear vs. editar) se determina por la presencia del EXTRA_GROUP_ID en el Intent.
 */
class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGroupBinding
    private val viewModel: CreateGroupViewModel by viewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var isEditMode = false
    private var groupId: Int? = null

    companion object {
        const val EXTRA_GROUP_ID = "GROUP_ID"
    }

    /**
     * Punto de entrada de la actividad. Configura la vista, determina el modo de operación
     * y llama a las funciones de inicialización.
     */
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

    /**
     * Configura los elementos de la interfaz (títulos, textos de botones) según si
     * la pantalla está en modo de creación o edición. En modo edición, solicita la carga de datos del grupo.
     */
    private fun setupUI() {
        if (isEditMode) {
            binding.textViewTitle.text = getString(R.string.edit_group_title)
            binding.buttonSaveGroup.text = getString(R.string.save_changes)
            groupId?.let {
                if (it != -1) {
                    viewModel.loadGroupData(it)
                }
            }
        } else {
            binding.textViewTitle.text = getString(R.string.create_group_title)
            binding.buttonSaveGroup.text = getString(R.string.create_group_button)
        }
    }

    /**
     * Configura los observadores de LiveData para reaccionar a los cambios del ViewModel.
     * Gestiona el estado de carga, la visualización de mensajes y el llenado de datos en modo edición.
     */
    private fun setupObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
        })

        viewModel.toastMessage.observe(this, Observer { messageResId ->
            Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
        })

        viewModel.grupo.observe(this, Observer { grupo ->
            binding.editTextGroupName.setText(grupo.nombre)
            binding.editTextGroupDescription.setText(grupo.descripcion)
        })

        viewModel.operationComplete.observe(this, Observer { isComplete ->
            if (isComplete) {
                finish()
                viewModel.onOperationCompleteHandled()
            }
        })
    }

    /**
     * Configura el listener del botón principal. Recoge los datos de la UI, los valida
     * y llama a la función correspondiente del ViewModel (crear o actualizar).
     */
    private fun setupListeners() {
        binding.buttonSaveGroup.setOnClickListener {
            val name = binding.editTextGroupName.text.toString().trim()
            val description = binding.editTextGroupDescription.text.toString().trim()
            val userId = firebaseAuth.currentUser?.uid

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, getString(R.string.create_group_empty_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == null) {
                Toast.makeText(this, getString(R.string.error_unauthorized_user), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                groupId?.let { viewModel.updateGroup(it, name, description) }
            } else {
                viewModel.createGroup(name, description, userId)
            }
        }
    }

    /**
     * Muestra u oculta la barra de progreso y gestiona la interactividad del botón principal.
     * @param isLoading True si se debe mostrar el indicador de carga, false en caso contrario.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSaveGroup.isEnabled = !isLoading
    }
}
