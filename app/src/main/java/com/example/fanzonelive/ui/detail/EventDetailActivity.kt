package com.example.fanzonelive.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityEventDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EventDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra("eventId") ?: return
        val title = intent.getStringExtra("title") ?: ""
        val match = intent.getStringExtra("match") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val emoji = intent.getStringExtra("emoji") ?: "⚽"
        val hostId = intent.getStringExtra("hostId") ?: ""
        val maxAttendees = intent.getIntExtra("maxAttendees", 10)

        binding.tvEmoji.text = emoji
        binding.tvTitle.text = title
        binding.tvMatch.text = match
        binding.tvDate.text = "📅 $date"
        binding.tvLocation.text = "📍 $location"
        binding.tvCapacity.text = "👥 Cupo máximo: $maxAttendees personas"

        binding.btnBack.setOnClickListener { finish() }

        val uid = auth.currentUser?.uid
        // El anfitrión no se solicita a sí mismo
        if (uid != null && uid == hostId) {
            binding.btnJoin.isEnabled = false
            binding.btnJoin.text = "ERES EL ANFITRIÓN 🎙️"
        }

        binding.btnJoin.setOnClickListener {
            val userId = auth.currentUser?.uid ?: return@setOnClickListener
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
    }
}
