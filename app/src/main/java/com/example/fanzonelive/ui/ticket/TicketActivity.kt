package com.example.fanzonelive.ui.ticket

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityTicketBinding
import com.example.fanzonelive.util.QrUtil
import com.google.firebase.auth.FirebaseAuth

class TicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra("eventId") ?: ""
        val title = intent.getStringExtra("eventTitle") ?: "Evento"
        val date = intent.getStringExtra("date") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "invitado"

        binding.tvTicketTitle.text = title
        binding.tvTicketDate.text = "📅 $date"
        binding.tvTicketLocation.text = "📍 $location"
        binding.tvUserId.text = "ID: ${uid.take(12).uppercase()}"

        binding.btnBack.setOnClickListener { finish() }

        // El contenido del QR identifica al asistente y al evento
        val payload = "FANZONE|$eventId|$uid"
        try {
            binding.ivQr.setImageBitmap(QrUtil.generate(payload, 600))
        } catch (_: Exception) { }
    }
}
