package com.example.fanzonelive.ui.sport
import android.os.Bundle
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
            "Fútbol","Mundial","Futbol" -> loadFootball()
            "Basket" -> loadBasket()
            "F1" -> loadF1()
            else -> loadFirestore(sport)
        }
    }
    private fun show(events: List<Event>) {
        binding.rvSportEvents.layoutManager = LinearLayoutManager(this)
        binding.rvSportEvents.adapter = EventAdapter(events.toMutableList())
    }
    private fun loadFootball() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        client.newCall(Request.Builder().url("https://v3.football.api-sports.io/fixtures?date=$today")
            .addHeader("x-apisports-key", apiKey).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { runOnUiThread { mundialFallback() } }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val arr = JSONObject(response.body?.string() ?: "").getJSONArray("response")
                        val list = mutableListOf<Event>()
                        for (i in 0 until minOf(arr.length(), 20)) {
                            val f = arr.getJSONObject(i)
                            val t = f.getJSONObject("teams")
                            list.add(Event(id=f.getJSONObject("fixture").getInt("id").toString(),
                                title=t.getJSONObject("home").getString("name")+" vs "+t.getJSONObject("away").getString("name"),
                                match=f.getJSONObject("league").getString("name"),
                                date="Hoy · "+f.getJSONObject("fixture").getString("date").substring(11,16),
                                emoji="⚽", sport="Fútbol", maxAttendees=15))
                        }
                        runOnUiThread { if (list.isEmpty()) mundialFallback() else show(list) }
                    } catch (e: Exception) { runOnUiThread { mundialFallback() } }
                }
            })
    }
    private fun loadBasket() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        client.newCall(Request.Builder().url("https://v1.basketball.api-sports.io/games?date=$today&league=12&season=2024-2025")
            .addHeader("x-apisports-key", apiKey).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { runOnUiThread { loadFirestore("Basket") } }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val arr = JSONObject(response.body?.string() ?: "").getJSONArray("response")
                        val list = mutableListOf<Event>()
                        for (i in 0 until minOf(arr.length(), 10)) {
                            val g = arr.getJSONObject(i); val t = g.getJSONObject("teams")
                            list.add(Event(id=i.toString(),
                                title=t.getJSONObject("home").getString("name")+" vs "+t.getJSONObject("away").getString("name"),
                                match="NBA 2024-25",
                                date="Hoy · "+g.getJSONObject("date").getString("start").substring(11,16),
                                emoji="🏀", sport="Basket", maxAttendees=12))
                        }
                        runOnUiThread { if (list.isEmpty()) loadFirestore("Basket") else show(list) }
                    } catch (e: Exception) { runOnUiThread { loadFirestore("Basket") } }
                }
            })
    }
    private fun loadF1() {
        client.newCall(Request.Builder().url("https://v1.formula-1.api-sports.io/races?season=2026&type=Race")
            .addHeader("x-apisports-key", apiKey).build())
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) { runOnUiThread { loadFirestore("F1") } }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val arr = JSONObject(response.body?.string() ?: "").getJSONArray("response")
                        val list = mutableListOf<Event>()
                        for (i in 0 until minOf(arr.length(), 10)) {
                            val r = arr.getJSONObject(i)
                            list.add(Event(id=i.toString(), title=r.getString("competition"),
                                match="Formula 1 2026", date=r.getString("date"),
                                emoji="🏎️", sport="F1", maxAttendees=8))
                        }
                        runOnUiThread { if (list.isEmpty()) loadFirestore("F1") else show(list) }
                    } catch (e: Exception) { runOnUiThread { loadFirestore("F1") } }
                }
            })
    }
    private fun mundialFallback() {
        show(listOf(
            Event(id="m1",title="México vs Sudáfrica",match="Grupo A - Mundial 2026",date="11 Jun · 13:00",emoji="⚽",sport="Fútbol",maxAttendees=20),
            Event(id="m2",title="Brasil vs Marruecos",match="Grupo C - Mundial 2026",date="13 Jun · 16:00",emoji="⚽",sport="Fútbol",maxAttendees=25),
            Event(id="m3",title="España vs Cabo Verde",match="Grupo H - Mundial 2026",date="15 Jun · 10:00",emoji="⚽",sport="Fútbol",maxAttendees=18),
            Event(id="m4",title="Argentina vs Austria",match="Grupo J - Mundial 2026",date="22 Jun · 11:00",emoji="⚽",sport="Fútbol",maxAttendees=30),
            Event(id="m5",title="México vs Corea del Sur",match="Grupo A - Mundial 2026",date="18 Jun · 19:00",emoji="⚽",sport="Fútbol",maxAttendees=20),
            Event(id="m6",title="FINAL Mundial 2026 🏆",match="Gran Final",date="19 Jul · 13:00",emoji="🏆",sport="Fútbol",maxAttendees=30)
        ))
    }
    private fun loadFirestore(sport: String) {
        db.collection("events").whereEqualTo("sport", sport).get().addOnSuccessListener { docs ->
            show(docs.map { doc -> Event(id=doc.id, title=doc.getString("title")?:"",
                match=doc.getString("match")?:"", date=doc.getString("date")?:"",
                location=doc.getString("location")?:"", hostId=doc.getString("hostId")?:"",
                maxAttendees=doc.getLong("maxAttendees")?.toInt()?:10,
                emoji=doc.getString("emoji")?:"⚽", sport=doc.getString("sport")?:"") })
        }
    }
}