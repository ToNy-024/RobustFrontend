package com.example.robustfrontend.data.model

data class Actividad(
    val idAct: Int,
    val nombre: String,
    val descripcion: String,

    val frecuencia: String,
    val dificultad: Int,
    val desagradable: Int,
    val puntaje: Int,
    val idGru: Int,
    val fCreacion: String,
    val creador: String,
    val estado: String // "pendiente", "aprobada", "rechazada"
)