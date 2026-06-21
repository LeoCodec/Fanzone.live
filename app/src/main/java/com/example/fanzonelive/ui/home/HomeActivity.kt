package com.example.fanzonelive.ui.home
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private val allEvents = mutableListOf<Event>()
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
        binding.tvProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        buildChips()
        setupSearch()
        setupBottomNav()
        val uid = auth.currentUser?.uid ?: ""
        SeedData.seedIfEmpty(db, uid) { loadEvents() }
    }
    override fun onResume() { super.onResume(); loadEvents() }
    private fun buildChips() {
        binding.chipContainer.removeAllViews(); chips.clear()
        Sports.filters.forEachIndexed { index, (name, emoji) ->
            val chip = TextView(this).apply {
                text = "$emoji $name"; textSize = 13f
                setPadding(dp(16), dp(8), dp(16), dp(8))
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.marginEnd = dp(8); layoutParams = lp
                setOnClickListener { currentSport = if (name == "Todo") null else name; selectChip(this); applyFilter() }
            }
            chips.add(chip); binding.chipContainer.addView(chip)
            if (index == 0) selectChip(chip)
        }
    }
    private fun selectChip(sel: TextView) {
        chips.forEach { if (it == sel) { it.setBackgroundResource(R.drawable.bg_filter_active); it.setTextColor(Color.WHITE) }
                        else { it.setBackgroundResource(R.drawable.bg_filter); it.setTextColor(Color.parseColor("#AAAAAA")) } }
    }
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) { applyFilter() }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun loadEvents() {
        db.collection("events").get().addOnSuccessListener { docs ->
            allEvents.clear()
            allEvents.addAll(docs.map { doc ->
                Event(id=doc.id, title=doc.getString("title")?:"", match=doc.getString("match")?:"",
                    date=doc.getString("date")?:"", location=doc.getString("location")?:"",
                    hostId=doc.getString("hostId")?:"",
                    maxAttendees=doc.getLong("maxAttendees")?.toInt()?:10,
                    taken=doc.getLong("taken")?.toInt()?:0,
                    fee=doc.getString("fee")?:"Gratis",
                    emoji=doc.getString("emoji")?:"âš½", sport=doc.getString("sport")?:"")
            })
            applyFilter()
        }
    }
    private fun applyFilter() {
        val query = binding.etSearch.text.toString().lowercase().trim()
        var filtered = if (currentSport == null) allEvents.toList()
                       else allEvents.filter { it.sport == currentSport }
        if (query.isNotEmpty()) filtered = filtered.filter {
            it.title.lowercase().contains(query) || it.sport.lowercase().contains(query) ||
            it.location.lowercase().contains(query) || it.match.lowercase().contains(query)
        }
        adapter.updateData(filtered)
        binding.tvEmptyHome.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }
    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { loadEvents(); true }
                R.id.nav_search -> true
                R.id.nav_create -> { startActivity(Intent(this, CreateEventActivity::class.java)); true }
                R.id.nav_requests -> { startActivity(Intent(this, NotificationsActivity::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> false
            }
        }
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}