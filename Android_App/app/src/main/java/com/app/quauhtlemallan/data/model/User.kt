package com.app.quauhtlemallan.data.model

import java.io.Serializable

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val country: String = "Guatemala",
    val profileImage: String = "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e",
    val score: Int = 0,
    val achievements: Int = 0,
    val porcentajeDescubierto: Int = 0,
    val insignias: Map<String, Int> = initializeInsignias()
) : Serializable

fun initializeInsignias(): Map<String, Int> {
    return mapOf(
        "art1" to 0, "art2" to 0, "art3" to 0, "art4" to 0,
        "cu1" to 0, "cu2" to 0, "cu3" to 0, "cu4" to 0,
        "dep1" to 0, "dep2" to 0, "dep3" to 0, "dep4" to 0,
        "gas1" to 0, "gas2" to 0, "gas3" to 0, "gas4" to 0,
        "geo1" to 0, "geo2" to 0, "geo3" to 0, "geo4" to 0, "geo5" to 0, "geo6" to 0, "geo7" to 0,
        "his1" to 0, "his2" to 0, "his3" to 0, "his4" to 0,
        "idi1" to 0, "idi2" to 0, "idi3" to 0, "idi4" to 0, "idi5" to 0,
        "tur1" to 0, "tur2" to 0, "tur3" to 0, "tur4" to 0
    )
}