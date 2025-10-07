package com.example.robustfrontend.data.model

data class ActividadUsuario(
    val idActUsu: Int,
    val idAct: Int,
    val idUsu: String, // Coincide con el tipo de dato de Usuario.idUsu
    val fechaCompletada: String, // Puedes usar un tipo Date/LocalDate si usas un TypeConverter con GSON/Room
    val comentario: String?, // El comentario puede ser opcional
    val puntajeObtenido: Int
)