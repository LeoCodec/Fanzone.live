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

    // Si llega un eventId entramos en modo EDICIÓN (UPDATE). Si no, es CREATE.
    private var editingId: String? = null

    private val sports = listOf("Fútbol", "Basket", "F1", "Box", "UFC", "Béisbol", "Mundial", "Otro")
    private val emojiMap = mapOf(
        "Fútbol" to "⚽", "Basket" to "🏀", "F1" to "🏎️",
        "Box" to "🥊", "UFC" to "🥊", "Béisbol" to "⚾", "Mundial" to "🏆", "Otro" to "🎯"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }

        editingId = intent.getStringExtra("eventId")

        if (editingId != null) {
            // ---- Modo EDICIÓN: precargamos los datos ----
            binding.tvScreenTitle.text = "Editar Evento"
            binding.btnCreate.text = "GUARDAR CAMBIOS 💾"
            binding.etTitle.setText(intent.getStringExtra("title"))
            binding.etMatch.setText(intent.getStringExtra("match"))
            binding.etDate.setText(intent.getStringExtra("date"))
            binding.etLocation.setText(intent.getStringExtra("location"))
            binding.etMax.setText(intent.getIntExtra("maxAttendees", 10).toString())
            val sport = intent.getStringExtra("sport") ?: "Fútbol"
            val idx = sports.indexOf(sport)
            if (idx >= 0) binding.spinnerSport.setSelection(idx)
        }

        binding.btnCreate.setOnClickListener { saveEvent() }
    }

    private fun saveEvent() {
        val title = binding.etTitle.text.toString().trim()
        val match = binding.etMatch.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val maxStr = binding.etMax.text.toString().trim()
        val sport = binding.spinnerSport.selectedItem.toString()

        if (title.isEmpty() || match.isEmpty() || date.isEmpty() || location.isEmpty() || maxStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "title" to title,
            "match" to match,
            "date" to date,
            "location" to location,
            "maxAttendees" to (maxStr.toIntOrNull() ?: 10),
            "sport" to sport,
            "emoji" to (emojiMap[sport] ?: "⚽"),
            "hostId" to (auth.currentUser?.uid ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        val id = editingId
        if (id != null) {
            // UPDATE
            db.collection("events").document(id)
                .update(data as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Evento actualizado ✏️", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        } else {
            // CREATE
            db.collection("events").add(data)
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
