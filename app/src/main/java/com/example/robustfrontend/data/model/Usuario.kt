package com.example.robustfrontend.data.model

data class Usuario(
    val idUsu: String,
    val nombre: String,
    val puntajeMes: Int,
    val puntajeTotal: Int,
    val fechaRegistro: String,
    val ultimaActividad: String?,
    val imagen: String?,
    val idGru: Int?
)
