package com.example.fanzonelive.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityEventDetailBinding
import com.example.fanzonelive.ui.chat.ChatActivity
import com.example.fanzonelive.ui.ticket.TicketActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var eventId = ""
    private var title = ""
    private var hostId = ""
    private var date = ""
    private var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getStringExtra("eventId") ?: return
        title = intent.getStringExtra("title") ?: ""
        val match = intent.getStringExtra("match") ?: ""
        date = intent.getStringExtra("date") ?: ""
        location = intent.getStringExtra("location") ?: ""
        val emoji = intent.getStringExtra("emoji") ?: "⚽"
        hostId = intent.getStringExtra("hostId") ?: ""
        val maxAttendees = intent.getIntExtra("maxAttendees", 10)
        val fee = intent.getStringExtra("fee") ?: "Gratis"

        binding.tvEmoji.text = emoji
        binding.tvTitle.text = title
        binding.tvMatch.text = match
        binding.tvDate.text = "📅 $date"
        binding.tvLocation.text = "📍 $location"
        binding.tvCapacity.text = "👥 Cupo máximo: $maxAttendees personas"
        binding.tvFee.text = "🎟️ Aporte: $fee"

        binding.btnBack.setOnClickListener { finish() }

        val uid = auth.currentUser?.uid
        if (uid != null && uid == hostId) {
            binding.btnJoin.isEnabled = false
            binding.btnJoin.text = "ERES EL ANFITRIÓN 🎙️"
        }

        binding.btnJoin.setOnClickListener { sendRequest() }
        binding.btnChat.setOnClickListener { ifAllowed { openChat() } }
        binding.btnTicket.setOnClickListener { ifAllowed { openTicket() } }
    }

    private fun sendRequest() {
        val userId = auth.currentUser?.uid ?: return
        val request = hashMapOf(
            "userId" to userId,
            "userEmail" to (auth.currentUser?.email ?: ""),
            "eventId" to eventId,
            "eventTitle" to title,
            "hostId" to hostId,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("requests").add(request)
            .addOnSuccessListener {
                Toast.makeText(this, "Solicitud enviada al anfitrión ✅", Toast.LENGTH_SHORT).show()
                binding.btnJoin.isEnabled = false
                binding.btnJoin.text = "SOLICITUD ENVIADA"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar solicitud", Toast.LENGTH_SHORT).show()
            }
    }

    // Verifica acceso (host o aceptado) antes de ejecutar la acción
    private fun ifAllowed(action: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        if (uid == hostId) { action(); return }
        db.collection("events").document(eventId).get()
            .addOnSuccessListener { doc ->
                val attendees = (doc.get("attendees") as? List<*>)?.map { it.toString() } ?: emptyList()
                if (attendees.contains(uid)) action()
                else Toast.makeText(this,
                    "El anfitrión debe aceptarte primero 🔒", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo verificar el acceso", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openChat() {
        startActivity(Intent(this, ChatActivity::class.java).apply {
            putExtra("eventId", eventId)
            putExtra("eventTitle", title)
        })
    }

    private fun openTicket() {
        startActivity(Intent(this, TicketActivity::class.java).apply {
            putExtra("eventId", eventId)
            putExtra("eventTitle", title)
            putExtra("date", date)
            putExtra("location", location)
        })
    }
}
