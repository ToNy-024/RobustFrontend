package com.example.robustfrontend.data.model

data class Grupo(
    val idGru: Int,
    val nombre: String,
    val descripcion: String,
    val fechaCreacion: String,
    val codigoInvitacion: String,
    val creador: String,
    val imagen: String?
)