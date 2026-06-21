package com.example.fanzonelive.ui.sport

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fanzonelive.databinding.ActivitySportBinding
import com.example.fanzonelive.model.Event
import com.example.fanzonelive.ui.home.EventAdapter
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySportBinding
    private val db = FirebaseFirestore.getInstance()
    private val apiKey = "cf8198595f85bc95872a9bf69851b3f8"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sport = intent.getStringExtra("sport") ?: "Fútbol"
        val emoji = intent.getStringExtra("emoji") ?: "⚽"

        binding.tvSportTitle.text = "$emoji $sport"
        binding.btnBack.setOnClickListener { finish() }

        when (sport) {
            "Fútbol", "Mundial" -> loadFootballMatches()
            "Basket" -> loadBasketMatches()
            "F1" -> loadF1Races()
            else -> loadFirestoreEvents(sport)
        }
    }

    private fun loadFootballMatches() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        val request = Request.Builder()
            .url("https://v3.football.api-sports.io/fixtures?date=$today")
            .addHeader("x-apisports-key", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { loadMundialFallback() }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                try {
                    val json = JSONObject(body)
                    val fixtures = json.getJSONArray("response")
                    val events = mutableListOf<Event>()

                    for (i in 0 until minOf(fixtures.length(), 20)) {
                        val fixture = fixtures.getJSONObject(i)
                        val teams = fixture.getJSONObject("teams")
                        val home = teams.getJSONObject("home").getString("name")
                        val away = teams.getJSONObject("away").getString("name")
                        val league = fixture.getJSONObject("league").getString("name")
                        val dateStr = fixture.getJSONObject("fixture").getString("date")
                        val hour = dateStr.substring(11, 16)

                        events.add(Event(
                            id = fixture.getJSONObject("fixture").getInt("id").toString(),
                            title = "$home vs $away ⚽",
                            match = league,
                            date = "Hoy · $hour CST",
                            location = "Tu colonia, CDMX",
                            emoji = "⚽",
                            sport = "Fútbol",
                            maxAttendees = 15
                        ))
                    }

                    runOnUiThread {
                        if (events.isEmpty()) loadMundialFallback()
                        else {
                            val adapter = EventAdapter(events)
                            binding.rvSportEvents.layoutManager = LinearLayoutManager(this@SportActivity)
                            binding.rvSportEvents.adapter = adapter
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread { loadMundialFallback() }
                }
            }
        })
    }

    private fun loadBasketMatches() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        val request = Request.Builder()
            .url("https://v1.basketball.api-sports.io/games?date=$today&league=12&season=2024-2025")
            .addHeader("x-apisports-key", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { loadFirestoreEvents("Basket") }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                try {
                    val json = JSONObject(body)
                    val games = json.getJSONArray("response")
                    val events = mutableListOf<Event>()

                    for (i in 0 until minOf(games.length(), 10)) {
                        val game = games.getJSONObject(i)
                        val teams = game.getJSONObject("teams")
                        val home = teams.getJSONObject("home").getString("name")
                        val away = teams.getJSONObject("away").getString("name")
                        val dateStr = game.getJSONObject("date").getString("start")
                        val hour = dateStr.substring(11, 16)

                        events.add(Event(
                            id = i.toString(),
                            title = "$home vs $away 🏀",
                            match = "NBA 2024-25",
                            date = "Hoy · $hour CST",
                            location = "Tu colonia, CDMX",
                            emoji = "🏀",
                            sport = "Basket",
                            maxAttendees = 12
                        ))
                    }

                    runOnUiThread {
                        if (events.isEmpty()) loadFirestoreEvents("Basket")
                        else {
                            val adapter = EventAdapter(events)
                            binding.rvSportEvents.layoutManager = LinearLayoutManager(this@SportActivity)
                            binding.rvSportEvents.adapter = adapter
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread { loadFirestoreEvents("Basket") }
                }
            }
        })
    }

    private fun loadF1Races() {
        val request = Request.Builder()
            .url("https://v1.formula-1.api-sports.io/races?season=2026&type=Race")
            .addHeader("x-apisports-key", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { loadFirestoreEvents("F1") }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                try {
                    val json = JSONObject(body)
                    val races = json.getJSONArray("response")
                    val events = mutableListOf<Event>()

                    for (i in 0 until minOf(races.length(), 10)) {
                        val race = races.getJSONObject(i)
                        val name = race.getString("competition")
                        val date = race.getString("date")

                        events.add(Event(
                            id = i.toString(),
                            title = "🏎️ $name",
                            match = "Formula 1 2026",
                            date = date,
                            location = "Tu colonia, CDMX",
                            emoji = "🏎️",
                            sport = "F1",
                            maxAttendees = 8
                        ))
                    }

                    runOnUiThread {
                        if (events.isEmpty()) loadFirestoreEvents("F1")
                        else {
                            val adapter = EventAdapter(events)
                            binding.rvSportEvents.layoutManager = LinearLayoutManager(this@SportActivity)
                            binding.rvSportEvents.adapter = adapter
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread { loadFirestoreEvents("F1") }
                }
            }
        })
    }

    private fun loadMundialFallback() {
        val matches = listOf(
            Event(id="m1", title="México vs Sudáfrica 🇲🇽", match="Grupo A - Mundial 2026", date="11 Jun · 1:00 p.m.", location="Col. Condesa, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=20),
            Event(id="m2", title="Brasil vs Marruecos 🇧🇷", match="Grupo C - Mundial 2026", date="13 Jun · 4:00 p.m.", location="Col. Roma, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=25),
            Event(id="m3", title="España vs Cabo Verde 🇪🇸", match="Grupo H - Mundial 2026", date="15 Jun · 10:00 a.m.", location="Col. Polanco, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=18),
            Event(id="m4", title="Argentina vs Austria 🇦🇷", match="Grupo J - Mundial 2026", date="22 Jun · 11:00 a.m.", location="Col. Narvarte, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=30),
            Event(id="m5", title="México vs Corea del Sur 🇲🇽", match="Grupo A - Mundial 2026", date="18 Jun · 7:00 p.m.", location="Col. Condesa, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=20),
            Event(id="m6", title="Colombia vs Portugal 🇨🇴", match="Grupo K - Mundial 2026", date="27 Jun · 5:30 p.m.", location="Col. Del Valle, CDMX", emoji="⚽", sport="Fútbol", maxAttendees=20),
            Event(id="m7", title="FINAL Mundial 2026 🏆", match="Gran Final", date="19 Jul · 1:00 p.m.", location="Tu casa, CDMX", emoji="🏆", sport="Fútbol", maxAttendees=30),
        )
        val adapter = EventAdapter(matches.toMutableList())
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
                val adapter = EventAdapter(events.toMutableList())
                binding.rvSportEvents.layoutManager = LinearLayoutManager(this)
                binding.rvSportEvents.adapter = adapter
            }
    }
}