package com.example.robustfrontend.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.load
import com.example.robustfrontend.R
import com.example.robustfrontend.databinding.ActivityEditUserBinding
import com.example.robustfrontend.viewmodel.admin.EditUserViewModel

class EditUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserBinding
    private val viewModel: EditUserViewModel by viewModels()
    private var userId: String? = null

    companion object {
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
    }

    /**
     * Punto de entrada de la actividad. Inicializa la vista, obtiene el ID de usuario
     * de los extras del Intent y llama a las funciones de configuración.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra(EXTRA_USER_ID)
        if (userId == null) {
            Toast.makeText(this, getString(R.string.admin_user_id_error), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupObservers()
        setupListeners()

        viewModel.fetchUser(userId!!)
    }

    /**
     * Configura los observadores de LiveData para reaccionar a los cambios del ViewModel.
     * Rellena la UI con los datos del usuario, gestiona el estado de carga y el éxito de la operación.
     */
    private fun setupObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBarEditUser.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.toastMessage.observe(this, Observer { messageResId ->
            Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
        })

        viewModel.user.observe(this, Observer { user ->
            binding.textViewUserNameEdit.text = user.nombre

            binding.imageViewUserAvatarEdit.load(user.imagen) {
                crossfade(true)
                placeholder(R.drawable.ic_avatar_placeholder)
                error(R.drawable.ic_avatar_placeholder)
            }

            // Si el usuario es admin, el switch se muestra chequeado y deshabilitado.
            if (user.esAdmin) {
                binding.switchAdminStatus.isChecked = true
                binding.switchAdminStatus.isEnabled = false
                binding.buttonSaveUserChanges.isEnabled = false
            } else {
                binding.switchAdminStatus.isChecked = false
                binding.switchAdminStatus.isEnabled = true
            }
        })

        viewModel.updateSuccess.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                finish() // Cierra la actividad si la actualización fue exitosa
                viewModel.onUpdateComplete()
            }
        })
    }

    /**
     * Configura el listener para el botón de guardar cambios. Llama al ViewModel
     * para actualizar el estado de administrador del usuario.
     */
    private fun setupListeners() {
        binding.buttonSaveUserChanges.setOnClickListener {
            val esAdmin = binding.switchAdminStatus.isChecked
            userId?.let { viewModel.updateUserAdminStatus(it, esAdmin) }
        }
    }
}
