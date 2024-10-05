package com.app.quauhtlemallan.data

data class User(
    val username: String = "",
    val email: String = "",
    val country: String = "",
    val profileImage: String = "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e",
    val score: Int = 0,
    val achievements: Int = 0
)