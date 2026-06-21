package com.example.fanzonelive.model

data class Event(
    val id: String = "",
    val title: String = "",
    val match: String = "",
    val date: String = "",
    val location: String = "",
    val hostId: String = "",
    val maxAttendees: Int = 10,
    val taken: Int = 0,
    val fee: String = "Gratis",
    val attendees: List<String> = emptyList(),
    val emoji: String = "⚽",
    val sport: String = "Fútbol"
)
