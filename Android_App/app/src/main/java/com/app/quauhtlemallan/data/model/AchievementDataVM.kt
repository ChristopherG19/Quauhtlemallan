package com.app.quauhtlemallan.data.model

data class AchievementData(
    val id: String,
    val title: String,
    val description: String = "",
    val imageUrl: String = "",
    val maxPoints: Int = 0
) {
    constructor() : this("", "", "", "", 0)
}

data class Category(
    val id: String,
    val title: String,
    val imageUrl: Int
)