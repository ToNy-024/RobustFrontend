package com.example.robustfrontend.data.model

/**
 * Representa a un usuario de la aplicación.
 */
data class Usuario(
    /** El identificador único del usuario, que coincide con el UID de Firebase. */
    val idUsu: String,
    /** El nombre visible del usuario. */
    val nombre: String,
    /** El puntaje acumulado por el usuario en el mes actual. */
    val puntajeMes: Int,
    /** El puntaje total acumulado por el usuario históricamente. */
    val puntajeTotal: Int,
    /** La fecha en que el usuario se registró. */
    val fechaRegistro: String,
    /** La fecha de la última actividad registrada por el usuario (opcional). */
    val ultimaActividad: String?,
    /** La URL a la imagen de perfil del usuario (opcional). */
    val imagen: String?,
    /** El ID del grupo al que pertenece el usuario (opcional, es nulo si no pertenece a ninguno). */
    val idGru: Int?,
    /** Un flag que indica si el usuario tiene permisos de administrador. */
    val esAdmin: Boolean
)
