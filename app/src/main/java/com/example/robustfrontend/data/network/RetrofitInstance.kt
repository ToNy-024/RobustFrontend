package com.example.robustfrontend.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que configura y provee una instancia de Retrofit para realizar
 * llamadas de red a la API. Utiliza un inicializador `lazy` para asegurar que la
 * instancia se cree solo una vez y cuando sea necesaria.
 */
object RetrofitInstance {

    // La URL base del backend. "10.0.2.2" es la IP especial para acceder al localhost
    // de la máquina anfitriona desde el emulador de Android.
    private const val BASE_URL = "https://robustbackend-p8z8.onrender.com/"

    /**
     * Crea un interceptor que registra en el Logcat los detalles de las peticiones
     * y respuestas de red. Es muy útil para la depuración.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // El nivel BODY muestra toda la información.
    }

    /**
     * Crea un cliente OkHttp personalizado que incluye el interceptor de logging.
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Configura la instancia principal de Retrofit. Se inicializa de forma perezosa (lazy).
     * Asocia la URL base, un conversor de JSON (Gson) y el cliente OkHttp personalizado.
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Asigna el cliente con el logger.
            .build()
    }

    /**
     * Expone la implementación de la interfaz ApiService, creada por Retrofit.
     * Esta es la propiedad que se usa en el resto de la aplicación para hacer las llamadas a la API.
     */
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
