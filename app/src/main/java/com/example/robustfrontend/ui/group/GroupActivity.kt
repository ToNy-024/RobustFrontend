package com.example.robustfrontend.ui.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.robustfrontend.databinding.ActivityGroupBinding
import com.example.robustfrontend.data.model.Grupo
import com.example.robustfrontend.viewmodel.Group.GroupViewModel
import com.google.firebase.auth.FirebaseAuth

class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupBinding
    // ¡TODO COMPLETADO! Se instancia el ViewModel
    private val viewModel: GroupViewModel by viewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentGroup: Grupo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
        setupListeners()

        // ¡TODO COMPLETADO! Reemplazamos la simulación con una llamada real al ViewModel
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            viewModel.loadUserData(userId)
        } else {
            // Manejar caso donde no hay usuario logueado
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            // Puedes añadir un ProgressBar si lo deseas
        })

        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })

        viewModel.grupo.observe(this, Observer { grupo ->
            currentGroup = grupo // Guardamos la referencia del grupo
            if (grupo != null) {
                showGroupInfo(grupo)
            } else {
                showNoGroupUI()
            }
        })

        viewModel.joinSuccess.observe(this, Observer { success ->
            if (success) {
                // Si la unión fue exitosa, recargamos los datos del usuario
                firebaseAuth.currentUser?.uid?.let { viewModel.loadUserData(it) }
                viewModel.onJoinSuccessComplete() // Resetea el evento
            }
        })

        // Observamos el usuario para saber si es el creador del grupo
        viewModel.usuario.observe(this, Observer { usuario ->
            // Actualizamos la visibilidad del botón de editar cuando tengamos ambos datos
            updateEditButtonVisibility()
        })
    }

    private fun showGroupInfo(grupo: Grupo) {
        binding.cardGroupInfo.visibility = View.VISIBLE
        binding.layoutNoGroup.visibility = View.GONE

        // ¡TODO COMPLETADO! Cargamos datos reales desde el ViewModel
        binding.textViewGroupName.text = grupo.nombre
        binding.textViewGroupDescription.text = grupo.descripcion
        binding.textViewInvitationCode.text = "Código: ${grupo.codigoInvitacion}"

        updateEditButtonVisibility()
    }

    private fun updateEditButtonVisibility() {
        val currentUser = viewModel.usuario.value
        val currentGroup = viewModel.grupo.value

        // ¡TODO COMPLETADO! Mostramos el botón solo si el ID del usuario actual es el creador del grupo
        val isCreator = currentUser != null && currentGroup != null && currentUser.idUsu == currentGroup.creador
        binding.buttonEditGroup.visibility = if (isCreator) View.VISIBLE else View.GONE
    }


    private fun showNoGroupUI() {
        binding.cardGroupInfo.visibility = View.GONE
        binding.layoutNoGroup.visibility = View.VISIBLE
    }

    private fun setupListeners() {
        binding.buttonJoinGroup.setOnClickListener {
            showJoinGroupDialog()
        }

        binding.buttonNavigateToCreate.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        binding.buttonEditGroup.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java)
            // ¡TODO COMPLETADO! Pasamos el ID del grupo para el modo edición
            currentGroup?.let {
                intent.putExtra(CreateGroupActivity.EXTRA_GROUP_ID, it.idGru)
            }
            startActivity(intent)
        }
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
                // ¡TODO COMPLETADO! Implementamos la llamada al ViewModel
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    viewModel.joinGroup(userId, code)
                } else {
                    Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show()
                }
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
