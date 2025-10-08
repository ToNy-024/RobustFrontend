package com.example.robustfrontend.data.model

/**
 * Representa la instancia de una actividad completada por un usuario en una fecha específica.
 */
data class ActividadUsuario(
    /** El identificador único de esta instancia de actividad completada. */
    val idActUsu: Int,
    /** El ID de la actividad que fue completada. */
    val idAct: Int,
    /** El ID del usuario que completó la actividad. */
    val idUsu: String,
    /** La fecha en que la actividad fue completada. */
    val fechaCompletada: String,
    /** Un comentario opcional que el usuario pudo haber añadido al completar la actividad. */
    val comentario: String?,
    /** El puntaje que el usuario obtuvo por completar esta actividad en esta instancia. */
    val puntajeObtenido: Int
)
