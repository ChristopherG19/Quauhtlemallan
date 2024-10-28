package com.app.quauhtlemallan.data.model

data class Question(
    val insignias: List<String> = listOf(),
    val pregunta: String = "",
    val puntos: Int = 0,
    val correcta: String = "",
    val respuestas: List<String> = listOf(),
    val tieneImagen: Boolean = false,
    val imagenUrl: String? = null,
    val datoExtra: String = ""
)

