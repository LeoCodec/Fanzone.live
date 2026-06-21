package com.example.fanzonelive.util

object Sports {
    // Chips del Home (incluye "Todo")
    val filters = listOf(
        "Todo" to "🔥",
        "Fútbol" to "⚽",
        "Basket" to "🏀",
        "F1" to "🏎️",
        "Box" to "🥊",
        "UFC/MMA" to "🥋",
        "Béisbol" to "⚾",
        "Jiu-jitsu" to "🤼",
        "Hockey" to "🏒",
        "Tenis" to "🎾",
        "NFL" to "🏈"
    )

    // Lista para el Spinner al crear evento (sin "Todo", con "Otro")
    val createList = filters.drop(1).map { it.first } + "Otro"

    val emoji = (filters + ("Otro" to "🎯")).toMap()

    fun emojiFor(sport: String) = emoji[sport] ?: "🎯"
}
