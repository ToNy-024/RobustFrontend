package com.example.robustfrontend.data.model

data class VotoActividad(
    val idVoto: Int,
    val idAct: Int,
    val idUsu: String, // Debe coincidir con el ID del usuario que vota
    val aprobado: Boolean, // true si el voto es a favor, false si es en contra
    val fechaVoto: String // Al igual que antes, podrías convertir esto a un tipo Date
)
