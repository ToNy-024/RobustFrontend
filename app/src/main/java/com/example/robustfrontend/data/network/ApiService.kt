package com.example.robustfrontend.data.network

import com.example.robustfrontend.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Endpoints de Actividad ---

    @GET("actividad/")
    suspend fun getActividades(): Response<List<Actividad>>

    @GET("actividad/{idAct}")
    suspend fun getActividad(@Path("idAct") idAct: Int): Response<Actividad>

    @GET("actividad/{idGru}") // Nota: Este endpoint en tu Flask parece ambiguo con el de arriba.
    suspend fun getActividadesPorGrupo(@Path("idGru") idGru: Int): Response<List<Actividad>>

    @POST("actividad/")
    suspend fun createActividad(@Body actividad: Actividad): Response<Map<String, String>>

    @PUT("actividad/{idAct}")
    suspend fun updateActividad(@Path("idAct") idAct: Int, @Body data: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, String>>

    @DELETE("actividad/{idAct}")
    suspend fun deleteActividad(@Path("idAct") idAct: Int): Response<Map<String, String>>


    // --- Endpoints de ActividadUsuario ---

    @GET("actividad_usuario/")
    suspend fun getActividadUsuarios(): Response<List<ActividadUsuario>>

    @GET("actividad_usuario/{idActUsu}")
    suspend fun getActividadUsuario(@Path("idActUsu") idActUsu: Int): Response<ActividadUsuario>

    @GET("actividad_usuario/usuario/{idUsu}")
    suspend fun getActividadesPorUsuario(@Path("idUsu") idUsu: String): Response<List<ActividadUsuario>>

    @GET("actividad_usuario/actividad/{idAct}")
    suspend fun getUsuariosPorActividad(@Path("idAct") idAct: Int): Response<List<ActividadUsuario>>

    @POST("actividad_usuario/")
    suspend fun createActividadUsuario(@Body actividadUsuario: ActividadUsuario): Response<Map<String, String>>

    @PUT("actividad_usuario/{idActUsu}")
    suspend fun updateActividadUsuario(@Path("idActUsu") idActUsu: Int, @Body data: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, String>>

    @DELETE("actividad_usuario/{idActUsu}")
    suspend fun deleteActividadUsuario(@Path("idActUsu") idActUsu: Int): Response<Map<String, String>>


    // --- Endpoints de Grupo ---

    @GET("grupo/")
    suspend fun getGrupos(): Response<List<Grupo>>

    @GET("grupo/{idGru}")
    suspend fun getGrupo(@Path("idGru") idGru: Int): Response<Grupo>

    @POST("grupo/")
    suspend fun createGrupo(@Body grupo: Grupo): Response<Map<String, String>>

    @PUT("grupo/{idGru}")
    suspend fun updateGrupo(@Path("idGru") idGru: Int, @Body data: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, String>>

    @DELETE("grupo/{idGru}")
    suspend fun deleteGrupo(@Path("idGru") idGru: Int): Response<Map<String, String>>


    // --- Endpoints de Usuario ---

    @GET("usuario/")
    suspend fun getUsuarios(): Response<List<Usuario>>

    @GET("usuario/{idUsu}")
    suspend fun getUsuario(@Path("idUsu") idUsu: String): Response<Usuario>

    @GET("usuario/{idGru}") // Nota: Este endpoint también parece ambiguo en tu Flask.
    suspend fun getUsuariosPorGrupo(@Path("idGru") idGru: Int): Response<List<Usuario>>

    @POST("usuario/")
    suspend fun createUsuario(@Body usuario: Usuario): Response<Map<String, String>>

    @PUT("usuario/{idUsu}")
    suspend fun updateUsuario(@Path("idUsu") idUsu: String, @Body data: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, String>>

    @DELETE("usuario/{idUsu}")
    suspend fun deleteUsuario(@Path("idUsu") idUsu: String): Response<Map<String, String>>


    // --- Endpoint de Votación ---

    @POST("actividad_usuario/votar") // Asegúrate que esta ruta sea correcta según tu configuración de Blueprints
    suspend fun votarActividad(@Body voto: VotoActividad): Response<Map<String, Any>>
}
