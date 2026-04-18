package com.example.fanzonelive.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fanzonelive.R
import com.example.fanzonelive.databinding.ActivityHomeBinding
import com.example.fanzonelive.model.Event
import com.example.fanzonelive.ui.create.CreateEventActivity
import com.example.fanzonelive.ui.profile.ProfileActivity
import com.example.fanzonelive.ui.sport.SportActivity
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val eventList = mutableListOf<Event>()
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EventAdapter(eventList)
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter

        binding.fabCreateEvent.setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }

        binding.tvProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setActiveFilter(binding.filterAll)

        binding.filterAll.setOnClickListener {
            setActiveFilter(it as TextView)
            loadEvents(null)
        }
        binding.filterFut.setOnClickListener {
            goToSport("Fútbol", "⚽")
        }
        binding.filterBasket.setOnClickListener {
            goToSport("Basket", "🏀")
        }
        binding.filterF1.setOnClickListener {
            goToSport("F1", "🏎️")
        }
        binding.filterMundial.setOnClickListener {
            goToSport("Mundial", "🏆")
        }

        loadEvents(null)
    }

    private fun goToSport(sport: String, emoji: String) {
        val intent = Intent(this, SportActivity::class.java).apply {
            putExtra("sport", sport)
            putExtra("emoji", emoji)
        }
        startActivity(intent)
    }

    private fun setActiveFilter(selected: TextView) {
        val filters = listOf(
            binding.filterAll, binding.filterFut,
            binding.filterBasket, binding.filterF1, binding.filterMundial
        )
        filters.forEach { filter ->
            if (filter == selected) {
                filter.setBackgroundResource(R.drawable.bg_filter_active)
                filter.setTextColor(Color.WHITE)
            } else {
                filter.setBackgroundResource(R.drawable.bg_filter)
                filter.setTextColor(Color.parseColor("#AAAAAA"))
            }
        }
    }

    private fun loadEvents(sport: String?) {
        val query = if (sport != null)
            db.collection("events").whereEqualTo("sport", sport)
        else
            db.collection("events")

        query.get().addOnSuccessListener { docs ->
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
            adapter.updateData(events)
        }
    }
}