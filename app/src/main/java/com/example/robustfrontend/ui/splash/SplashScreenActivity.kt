package com.example.robustfrontend.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.robustfrontend.R
import com.example.robustfrontend.ui.dashboard.DashboardActivity
import com.example.robustfrontend.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
/**
 * Actividad inicial de la aplicación. Muestra una pantalla de bienvenida durante un breve período
 * y luego redirige al usuario a la pantalla de Login o al Dashboard principal, según si
 * ya existe una sesión de usuario activa en Firebase.
 */
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    /**
     * Punto de entrada de la actividad.
     * Inicializa Firebase Auth y, tras una demora de 2.5 segundos, comprueba el estado
     * de autenticación del usuario para decidir a qué pantalla navegar.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Si hay un usuario, lo llevamos al Dashboard
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                // Si no, a la pantalla de Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
            // Cerramos la SplashScreen para que el usuario no pueda volver a ella
            finish()
        }, 2500)
    }
}
