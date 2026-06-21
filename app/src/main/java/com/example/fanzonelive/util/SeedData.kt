package com.example.fanzonelive.util

import com.google.firebase.firestore.FirebaseFirestore

object SeedData {

    // Siembra eventos de ejemplo SOLO si la colección está vacía.
    fun seedIfEmpty(db: FirebaseFirestore, hostId: String, onDone: () -> Unit) {
        db.collection("events").limit(1).get()
            .addOnSuccessListener { snap ->
                if (!snap.isEmpty) { onDone(); return@addOnSuccessListener }
                val batch = db.batch()
                for (e in samples()) {
                    val ref = db.collection("events").document()
                    val data = hashMapOf(
                        "title" to e[0], "match" to e[1], "date" to e[2],
                        "location" to e[3], "sport" to e[4],
                        "emoji" to Sports.emojiFor(e[4] as String),
                        "maxAttendees" to e[5], "taken" to e[6],
                        "hostId" to hostId, "timestamp" to System.currentTimeMillis()
                    )
                    batch.set(ref, data)
                }
                batch.commit().addOnSuccessListener { onDone() }
                    .addOnFailureListener { onDone() }
            }
            .addOnFailureListener { onDone() }
    }

    private fun samples(): List<List<Any>> = listOf(
        listOf("Vemos México vs Brasil","Grupo A - Mundial 2026","15 Jun · 18:00","Col. Roma, CDMX","Fútbol",20,12),
        listOf("Clásico en mi depa","Real Madrid vs Barcelona","21 Jun · 14:00","Col. Condesa, CDMX","Fútbol",15,15),
        listOf("Final Mundial 2026","Gran Final","19 Jul · 13:00","Col. Del Valle, CDMX","Fútbol",30,9),
        listOf("Noche de NBA","Lakers vs Celtics","18 Jun · 20:30","Col. Narvarte, CDMX","Basket",12,7),
        listOf("Playoffs Basket","Warriors vs Suns","23 Jun · 19:00","Col. Roma Norte, CDMX","Basket",10,4),
        listOf("GP de México F1","Gran Premio CDMX","26 Oct · 14:00","Col. Polanco, CDMX","F1",8,8),
        listOf("Carrera nocturna F1","GP Las Vegas","22 Nov · 22:00","Col. Juárez, CDMX","F1",6,2),
        listOf("Noche de Pelea","Canelo vs Charlo","28 Jun · 21:00","Col. Doctores, CDMX","Box",16,11),
        listOf("UFC Fight Night","Adesanya vs Pereira","20 Jun · 22:00","Col. Escandón, CDMX","UFC/MMA",14,6),
        listOf("Serie Mundial","Dodgers vs Yankees","25 Oct · 18:00","Col. Coyoacán, CDMX","Béisbol",18,5),
        listOf("Torneo Jiu-jitsu","ADCC Trials","24 Jun · 16:00","Col. Nápoles, CDMX","Jiu-jitsu",10,3),
        listOf("Hockey en vivo","Maple Leafs vs Bruins","27 Jun · 19:30","Col. San Rafael, CDMX","Hockey",12,4),
        listOf("Final de Tenis","Wimbledon - Final","13 Jul · 09:00","Col. Anzures, CDMX","Tenis",8,6),
        listOf("Super Bowl Party","Chiefs vs 49ers","08 Feb · 17:30","Col. Roma, CDMX","NFL",25,18)
    )
}
