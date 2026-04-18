package com.example.fanzonelive.ui.sport

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fanzonelive.databinding.ActivitySportBinding
import com.example.fanzonelive.model.Event
import com.example.fanzonelive.ui.detail.EventDetailActivity
import com.google.firebase.firestore.FirebaseFirestore

class SportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySportBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sport = intent.getStringExtra("sport") ?: "Fútbol"
        val color = intent.getStringExtra("color") ?: "#E53935"
        val emoji = intent.getStringExtra("emoji") ?: "⚽"

        binding.tvSportTitle.text = "$emoji $sport"
        binding.btnBack.setOnClickListener { finish() }

        // Partidos hardcoded del Mundial si es Fútbol
        if (sport == "Fútbol" || sport == "Mundial") {
            loadMundialMatches()
        } else {
            loadFirestoreEvents(sport)
        }
    }

    private fun loadMundialMatches() {
        val matches = listOf(
            Event(id="m1", title="México vs Sudáfrica 🇲🇽", match="Grupo A - Fase de grupos", date="11 Jun · 1:00 p.m.", location="Col. Condesa, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=20),
            Event(id="m2", title="Canadá vs Bosnia 🇨🇦", match="Grupo B - Fase de grupos", date="12 Jun · 1:00 p.m.", location="Col. Roma, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=15),
            Event(id="m3", title="Brasil vs Marruecos 🇧🇷", match="Grupo C - Fase de grupos", date="13 Jun · 4:00 p.m.", location="Col. Polanco, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=25),
            Event(id="m4", title="España vs Cabo Verde 🇪🇸", match="Grupo H - Fase de grupos", date="15 Jun · 10:00 a.m.", location="Col. Narvarte, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=18),
            Event(id="m5", title="Argentina vs Austria 🇦🇷", match="Grupo J - Fase de grupos", date="22 Jun · 11:00 a.m.", location="Col. Del Valle, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=30),
            Event(id="m6", title="México vs Corea del Sur 🇲🇽", match="Grupo A - Fase de grupos", date="18 Jun · 7:00 p.m.", location="Col. Condesa, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=20),
            Event(id="m7", title="Francia vs Senegal 🇫🇷", match="Grupo I - Fase de grupos", date="16 Jun · 1:00 p.m.", location="Col. Pedregal, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=15),
            Event(id="m8", title="Colombia vs Portugal 🇨🇴", match="Grupo K - Fase de grupos", date="27 Jun · 5:30 p.m.", location="Col. Satelite, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=20),
            Event(id="m9", title="Cuartos de Final 🏆", match="Mundial 2026 - Cuartos", date="9 Jul · 2:00 p.m.", location="Tu casa, CDMX", emoji="🏆", sport="Fútbol", maxAttendees=10),
            Event(id="m10", title="FINAL Mundial 2026 🏆", match="Gran Final - 19 Jul", date="19 Jul · 1:00 p.m.", location="Tu casa, CDMX", emoji="🏆", sport="Fútbol", maxAttendees=30),
        )
        val adapter = com.example.fanzonelive.ui.home.EventAdapter(matches.toMutableList())
        binding.rvSportEvents.layoutManager = LinearLayoutManager(this)
        binding.rvSportEvents.adapter = adapter
    }

    private fun loadFirestoreEvents(sport: String) {
        db.collection("events").whereEqualTo("sport", sport).get()
            .addOnSuccessListener { docs ->
                val events = docs.map { doc ->
                    Event(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        match = doc.getString("match") ?: "",
                        date = doc.getString("date") ?: "",
                        location = doc.getString("location") ?: "",
                        hostId = doc.getString("hostId") ?: "",
                        maxAttendees = doc.getLong("maxAttendees")?.toInt() ?: 10,
                        emoji = doc.getString("emoji") ?: "⚽",
                        sport = doc.getString("sport") ?: ""
                    )
                }
                val adapter = com.example.fanzonelive.ui.home.EventAdapter(events.toMutableList())
                binding.rvSportEvents.layoutManager = LinearLayoutManager(this)
                binding.rvSportEvents.adapter = adapter
            }
    }
}