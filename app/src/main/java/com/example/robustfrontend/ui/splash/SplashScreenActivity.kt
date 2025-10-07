package com.example.robustfrontend.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.robustfrontend.R
import com.example.robustfrontend.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        // Usamos un Handler para crear un retraso
        Handler(Looper.getMainLooper()).postDelayed({
            // Comprobamos si ya hay un usuario logueado
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Si hay usuario, vamos al Dashboard (o la actividad principal)
                // Debes crear esta actividad: DashboardActivity
                // val intent = Intent(this, DashboardActivity::class.java)
                // startActivity(intent)
                // Por ahora, lo mandamos al Login para seguir el flujo
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Si no hay usuario, vamos a la pantalla de Login
                startActivity(Intent(this, LoginActivity::class.java))
            }

            // Cerramos la SplashScreen para que el usuario no pueda volver a ella con el botón "atrás"
            finish()
        }, 2500) // 2500 milisegundos = 2.5 segundos
    }
}
