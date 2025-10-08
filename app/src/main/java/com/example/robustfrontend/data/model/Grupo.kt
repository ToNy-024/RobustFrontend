package com.example.robustfrontend.data.model

/**
 * Representa a un grupo de usuarios que colaboran en actividades.
 */
data class Grupo(
    /** El identificador único del grupo. */
    val idGru: Int,
    /** El nombre del grupo. */
    val nombre: String,
    /** Una descripción detallada del propósito o las reglas del grupo. */
    val descripcion: String,
    /** La fecha en que el grupo fue creado. */
    val fechaCreacion: String,
    /** El código único que los usuarios pueden usar para unirse a este grupo. */
    val codigoInvitacion: String,
    /** El ID del usuario que creó el grupo. */
    val creador: String,
    /** Una URL a una imagen de avatar o banner para el grupo (opcional). */
    val imagen: String?
)
