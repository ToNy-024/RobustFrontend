package com.example.robustfrontend.data.model

/**
 * Representa el voto de un usuario para una actividad específica.
 * Se utiliza para enviar la información del voto al backend.
 */
data class VotoActividad(
    /** El identificador único del voto (generalmente asignado por el backend). */
    val idVoto: Int,
    /** El ID de la actividad por la que se está votando. */
    val idAct: Int,
    /** El ID del usuario que emite el voto. */
    val idUsu: String,
    /** El sentido del voto: true si es a favor (aprobar), false si es en contra (rechazar). */
    val aprobado: Boolean,
    /** La fecha en que se emitió el voto (generalmente asignada por el backend). */
    val fechaVoto: String
)
