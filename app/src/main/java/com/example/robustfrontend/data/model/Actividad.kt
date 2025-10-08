package com.example.robustfrontend.data.model

/**
 * Representa una actividad o tarea dentro de un grupo.
 */
data class Actividad(
    /** El identificador único de la actividad. */
    val idAct: Int,
    /** El nombre de la actividad. */
    val nombre: String,
    /** Una descripción detallada de en qué consiste la actividad. */
    val descripcion: String,
    /** La frecuencia con la que la actividad puede/debe ser realizada (ej. "diaria", "semanal"). */
    val frecuencia: String,
    /** El nivel de dificultad de la actividad, en una escala (ej. 1-5). */
    val dificultad: Int,
    /** El nivel de desagrado o tedio de la actividad, en una escala (ej. 1-5). */
    val desagradable: Int,
    /** El puntaje base que otorga esta actividad al ser completada. */
    val puntaje: Int,
    /** El ID del grupo al que pertenece esta actividad. */
    val idGru: Int,
    /** La fecha de creación de la actividad (en formato String). */
    val fCreacion: String,
    /** El ID del usuario que propuso la actividad. */
    val creador: String,
    /** El estado actual de la actividad. Posibles valores: "pendiente", "aprobada", "en_progreso", "hecha". */
    val estado: String,

    // --- Campos para la votación ---
    /** El número de votos a favor que ha recibido la actividad. (Opcional) */
    val votos_a_favor: Int? = 0,
    /** El número total de miembros en el grupo en el momento de la consulta. (Opcional) */
    val total_miembros_grupo: Int? = 0,
    /** Un flag que indica si el usuario actual ya ha votado por esta actividad. (Opcional) */
    val ha_votado_usuario: Boolean? = false
)
