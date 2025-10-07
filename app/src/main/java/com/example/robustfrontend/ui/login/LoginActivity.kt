package com.example.robustfrontend.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.glance.visibility
import com.example.robustfrontend.databinding.ActivityLoginBinding // Importar ViewBinding
import com.example.robustfrontend.ui.dashboard.DashboardActivity // Actividad a la que irás después del login
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding // Usar ViewBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configurar ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Google Sign-In con el token de tu web client ID de Firebase
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.robustfrontend.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar el listener para el botón de login
        binding.signInButton.setOnClickListener {
            Log.d(TAG, "Botón de sign in presionado")
            signIn()
        }
    }

    private fun signIn() {
        showLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // Nuevo método para manejar el resultado del login con Google
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // El login con Google fue exitoso, ahora autenticamos con Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "Login con Google exitoso. ID del token: ${account.idToken}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // El login con Google falló
                Log.w(TAG, "Login con Google falló", e)
                showLoading(false)
                Toast.makeText(this, "Falló el inicio de sesión con Google.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // El usuario cerró la ventana de login sin seleccionar una cuenta
            Log.w(TAG, "El usuario canceló el flujo de login. Código: ${result.resultCode}")
            showLoading(false)
            Toast.makeText(this, "Inicio de sesión cancelado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login con Firebase exitoso
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "Login con Firebase exitoso: ${user?.displayName}")

                    // Aquí es donde deberías comprobar si el usuario es nuevo en tu backend
                    // y crearlo en tu base de datos si es necesario.

                    // TODO: Crear lógica para enviar datos de usuario al backend

                    // Navegar a la siguiente actividad
                    goToDashboard()

                } else {
                    // Si el login con Firebase falla
                    Log.w(TAG, "Fallo en signInWithCredential", task.exception)
                    showLoading(false)
                    Toast.makeText(this, "Falló la autenticación con Firebase.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToDashboard() {
        // TODO: Crea una `DashboardActivity` o cambia el nombre por tu actividad principal
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish() // Cierra LoginActivity para que el usuario no pueda volver a ella
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.signInButton.isEnabled = !isLoading
    }
}
