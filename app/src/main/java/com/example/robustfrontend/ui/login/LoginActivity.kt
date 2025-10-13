package com.example.robustfrontend.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.robustfrontend.R
import com.example.robustfrontend.databinding.ActivityLoginBinding
import com.example.robustfrontend.ui.dashboard.DashboardActivity
import com.example.robustfrontend.viewmodel.login.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: LoginViewModel by viewModels()

    companion object {
        private const val TAG = "LoginActivity"
    }

    /**
     * Punto de entrada de la actividad. Inicializa Firebase Auth, Google Sign-In,
     * la vinculación de vistas y llama a los métodos de configuración.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webClientId = getString(R.string.web_client_id)
        Log.d(TAG, "WEB_CLIENT_ID from resources: '$webClientId'")

        if (webClientId.isNullOrEmpty()) {
            Log.e(TAG, "WEB_CLIENT_ID is missing! Check local.properties and your build.gradle configuration.")
            Toast.makeText(this, "ERROR: WEB_CLIENT_ID is not configured.", Toast.LENGTH_LONG).show()
            return 
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        setupListeners()
        setupObservers()
    }

    /**
     * Configura el listener para el botón de inicio de sesión.
     */
    private fun setupListeners() {
        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    /**
     * Configura los observadores de LiveData para reaccionar a los cambios del ViewModel.
     * Navega al dashboard si el login es completo o muestra un mensaje Toast en caso de error.
     */
    private fun setupObservers() {
        viewModel.loginComplete.observe(this, Observer { isComplete ->
            if (isComplete) {
                goToDashboard()
            }
        })

        viewModel.toastMessage.observe(this, Observer { messageResId ->
            showLoading(false)
            Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
        })
    }

    /**
     * Inicia el flujo de inicio de sesión con Google lanzando el intent correspondiente.
     */
    private fun signIn() {
        showLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    /**
     * ActivityResultLauncher que gestiona el resultado del intent de Google Sign-In.
     * Si el resultado es exitoso, obtiene la cuenta y llama a la autenticación con Firebase.
     */
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                showLoading(false)
                Toast.makeText(this, getString(R.string.login_with_google_failed), Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w(TAG, "User cancelled login flow.")
            showLoading(false)
        }
    }

    /**
     * Se autentica con Firebase utilizando el token de ID de la cuenta de Google.
     * Si tiene éxito, pasa el control al ViewModel para gestionar el registro en el backend.
     * @param idToken El token de ID de Google para crear la credencial de Firebase.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        viewModel.handleUserLogin(user)
                    } else {
                        showLoading(false)
                        Toast.makeText(this, getString(R.string.login_firebase_user_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w(TAG, "signInWithCredential failed", task.exception)
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.login_firebase_auth_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Navega a la DashboardActivity y finaliza la actividad actual.
     */
    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Muestra u oculta la barra de progreso y gestiona la interactividad del botón de login.
     * @param isLoading True si debe mostrarse la carga, false en caso contrario.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signInButton.isEnabled = !isLoading
    }
}
