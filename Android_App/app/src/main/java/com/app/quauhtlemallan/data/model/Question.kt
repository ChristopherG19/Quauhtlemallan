package com.app.quauhtlemallan.data.model

data class Question(
    val insignias: List<String> = listOf(),
    val pregunta: String = "",
    val puntos: Int = 0,
    val correcta: String = "",
    val respuestas: List<String> = listOf(),
    val image: Boolean = false,
    val url: String? = null
)

