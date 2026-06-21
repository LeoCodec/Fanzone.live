package com.example.fanzonelive.ui.detail
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.R
import com.example.fanzonelive.ui.chat.ChatActivity
import com.example.fanzonelive.ui.ticket.TicketActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class EventDetailActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var eventId = ""; private var title = ""; private var hostId = ""
    private var date = ""; private var location = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        eventId = intent.getStringExtra("eventId") ?: return
        title = intent.getStringExtra("title") ?: ""
        val match = intent.getStringExtra("match") ?: ""
        date = intent.getStringExtra("date") ?: ""
        location = intent.getStringExtra("location") ?: ""
        val emoji = intent.getStringExtra("emoji") ?: "âš½"
        hostId = intent.getStringExtra("hostId") ?: ""
        val maxAttendees = intent.getIntExtra("maxAttendees", 10)
        val fee = intent.getStringExtra("fee") ?: "Gratis"
        val taken = intent.getIntExtra("taken", 0)
        findViewById<TextView>(R.id.tvEmoji).text = emoji
        findViewById<TextView>(R.id.tvTitle).text = title
        findViewById<TextView>(R.id.tvMatch).text = match
        findViewById<TextView>(R.id.tvDate).text = date
        findViewById<TextView>(R.id.tvLocation).text = location
        findViewById<TextView>(R.id.tvCapacity).text = "ðŸ‘¥ Cupo: $taken/$maxAttendees personas"
        findViewById<TextView>(R.id.tvFee).text = "ðŸŽŸï¸ $fee"
        val pb = findViewById<ProgressBar>(R.id.pbCapacity)
        pb.max = if (maxAttendees > 0) maxAttendees else 1
        pb.progress = taken.coerceAtMost(maxAttendees)
        val uid = auth.currentUser?.uid
        val btnJoin = findViewById<android.widget.Button>(R.id.btnJoin)
        if (uid != null && uid == hostId) { btnJoin.isEnabled = false; btnJoin.text = "ERES EL ANFITRIÃ“N ðŸŽ™ï¸" }
        btnJoin.setOnClickListener { sendRequest() }
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.btnChat).setOnClickListener { ifAllowed { openChat() } }
        findViewById<TextView>(R.id.btnTicket).setOnClickListener { ifAllowed { openTicket() } }
    }
    private fun sendRequest() {
        val userId = auth.currentUser?.uid ?: return
        val req = hashMapOf<String, Any>("userId" to userId,
            "userEmail" to (auth.currentUser?.email ?: ""),
            "eventId" to eventId, "eventTitle" to title,
            "hostId" to hostId, "status" to "pending",
            "timestamp" to System.currentTimeMillis())
        db.collection("requests").add(req)
            .addOnSuccessListener {
                Toast.makeText(this, "Solicitud enviada âœ…", Toast.LENGTH_SHORT).show()
                val btnJoin = findViewById<android.widget.Button>(R.id.btnJoin)
                btnJoin.isEnabled = false; btnJoin.text = "SOLICITUD ENVIADA"
            }
    }
    private fun ifAllowed(action: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        if (uid == hostId) { action(); return }
        db.collection("events").document(eventId).get().addOnSuccessListener { doc ->
            val attendees = (doc.get("attendees") as? List<*>)?.map { it.toString() } ?: emptyList()
            if (attendees.contains(uid)) action()
            else Toast.makeText(this, "El anfitriÃ³n debe aceptarte primero ðŸ”’", Toast.LENGTH_LONG).show()
        }
    }
    private fun openChat() { startActivity(Intent(this, ChatActivity::class.java).apply {
        putExtra("eventId", eventId); putExtra("eventTitle", title) }) }
    private fun openTicket() { startActivity(Intent(this, TicketActivity::class.java).apply {
        putExtra("eventId", eventId); putExtra("eventTitle", title)
        putExtra("date", date); putExtra("location", location) }) }
}