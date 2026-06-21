package com.example.fanzonelive.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fanzonelive.R
import com.example.fanzonelive.databinding.ActivityHomeBinding
import com.example.fanzonelive.model.Event
import com.example.fanzonelive.ui.create.CreateEventActivity
import com.example.fanzonelive.ui.notifications.NotificationsActivity
import com.example.fanzonelive.ui.profile.ProfileActivity
import com.example.fanzonelive.util.SeedData
import com.example.fanzonelive.util.Sports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val eventList = mutableListOf<Event>()
    private lateinit var adapter: EventAdapter
    private val chips = mutableListOf<TextView>()
    private var currentSport: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EventAdapter(eventList)
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter

        binding.tvProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        buildChips()
        setupBottomNav()

        // Siembra datos de ejemplo la primera vez y luego carga
        val uid = auth.currentUser?.uid ?: ""
        SeedData.seedIfEmpty(db, uid) { loadEvents(null) }
    }

    override fun onResume() {
        super.onResume()
        loadEvents(currentSport)
    }

    // ---- Chips de deportes generados dinámicamente ----
    private fun buildChips() {
        binding.chipContainer.removeAllViews()
        chips.clear()
        Sports.filters.forEachIndexed { index, (name, emoji) ->
            val chip = TextView(this).apply {
                text = "$emoji $name"
                textSize = 13f
                setPadding(dp(18), dp(9), dp(18), dp(9))
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.marginEnd = dp(8)
                layoutParams = lp
                setOnClickListener {
                    currentSport = if (name == "Todo") null else name
                    selectChip(this)
                    loadEvents(currentSport)
                }
            }
            chips.add(chip)
            binding.chipContainer.addView(chip)
            if (index == 0) selectChip(chip)
        }
    }

    private fun selectChip(selected: TextView) {
        chips.forEach {
            if (it == selected) {
                it.setBackgroundResource(R.drawable.bg_filter_active)
                it.setTextColor(Color.WHITE)
            } else {
                it.setBackgroundResource(R.drawable.bg_filter)
                it.setTextColor(Color.parseColor("#AAAAAA"))
            }
        }
    }

    // ---- Lectura / filtro desde Firestore ----
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
                    taken = doc.getLong("taken")?.toInt()
                        ?: (doc.get("attendees") as? List<*>)?.size ?: 0,
                    fee = doc.getString("fee") ?: "Gratis",
                    emoji = doc.getString("emoji") ?: Sports.emojiFor(doc.getString("sport") ?: ""),
                    sport = doc.getString("sport") ?: ""
                )
            }.sortedBy { it.date }
            adapter.updateData(events)
            binding.tvEmptyHome.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // ---- Navegación inferior ----
    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { loadEvents(currentSport); true }
                R.id.nav_search -> { /* enfoque en filtros */ true }
                R.id.nav_create -> {
                    startActivity(Intent(this, CreateEventActivity::class.java)); true
                }
                R.id.nav_requests -> {
                    startActivity(Intent(this, NotificationsActivity::class.java)); true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java)); true
                }
                else -> false
            }
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
