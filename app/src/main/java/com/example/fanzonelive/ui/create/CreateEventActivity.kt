package com.example.fanzonelive.ui.create

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityCreateEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateEventBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }

        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val match = binding.etMatch.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val maxStr = binding.etMax.text.toString().trim()
            val sport = binding.spinnerSport.selectedItem.toString()

            if (title.isEmpty() || match.isEmpty() || date.isEmpty() || location.isEmpty() || maxStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emojiMap = mapOf(
                "Fútbol" to "⚽", "Basket" to "🏀", "F1" to "🏎️",
                "Box" to "🥊", "UFC" to "🥊", "Béisbol" to "⚾", "Mundial" to "🏆"
            )

            val event = hashMapOf(
                "title" to title, "match" to match, "date" to date,
                "location" to location, "maxAttendees" to (maxStr.toIntOrNull() ?: 10),
                "sport" to sport, "emoji" to (emojiMap[sport] ?: "⚽"),
                "hostId" to (auth.currentUser?.uid ?: ""),
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("events").add(event)
                .addOnSuccessListener {
                    Toast.makeText(this, "Evento creado ✅", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al crear evento", Toast.LENGTH_SHORT).show()
                }
        }
    }
}