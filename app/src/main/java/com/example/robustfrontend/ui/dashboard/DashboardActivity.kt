package com.example.robustfrontend.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.robustfrontend.R
import com.example.robustfrontend.databinding.ActivityDashboardBinding
import com.example.robustfrontend.ui.admin.AdminUserActivity
import com.example.robustfrontend.ui.group.GroupActivity
import com.example.robustfrontend.viewmodel.Dashboard.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Punto de entrada de la actividad. Se encarga de la configuración inicial,
     * como la vinculación de vistas, la validación del usuario y la carga del fragmento inicial.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            finish() // Si no hay usuario, no debería estar aquí.
            return
        }

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        setupNavigation()
        setupObservers()

        viewModel.checkUserAdminStatus(currentUser.uid)
    }

    /**
     * Configura la barra de navegación inferior, gestionando los clics en los diferentes
     * ítems del menú para navegar a las secciones correspondientes.
     */
    private fun setupNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.navigation_group -> {
                    viewModel.onGroupNavigationSelected()
                    true
                }
                R.id.navigation_admin -> {
                    viewModel.onAdminNavigationSelected()
                    true
                }
                else -> false
            }
        }

        binding.fabAddOrJoinGroup.setOnClickListener {
            viewModel.onGroupNavigationSelected()
        }
    }

    /**
     * Configura los observadores de LiveData para reaccionar a los eventos del ViewModel,
     * como la navegación a otras actividades o la visualización de la pestaña de administrador.
     */
    private fun setupObservers() {
        viewModel.navigateToGroup.observe(this, Observer { navigate ->
            if (navigate) {
                startActivity(Intent(this, GroupActivity::class.java))
                viewModel.onNavigationComplete()
            }
        })

        viewModel.navigateToAdmin.observe(this, Observer { navigate ->
            if (navigate) {
                startActivity(Intent(this, AdminUserActivity::class.java))
                viewModel.onNavigationComplete()
            }
        })

        viewModel.isAdmin.observe(this, Observer { isAdmin ->
            binding.bottomNavigationView.menu.findItem(R.id.navigation_admin).isVisible = isAdmin
        })

        viewModel.toastMessage.observe(this, Observer { messageResId ->
            Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
        })
    }

    /**
     * Carga un fragmento en el contenedor principal de la actividad.
     * @param fragment El fragmento que se va a mostrar.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
