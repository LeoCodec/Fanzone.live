package com.example.fanzonelive.ui.create

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.R
import com.example.fanzonelive.databinding.ActivityCreateEventBinding
import com.example.fanzonelive.util.Sports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateEventBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var editingId: String? = null
    private val sports = Sports.createList
    private var selectedSpace = "Hogar / Depto"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }

        // Slider de cupo
        binding.tvCupoValue.text = binding.sbCupo.progress.toString()
        binding.sbCupo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, value: Int, fromUser: Boolean) {
                binding.tvCupoValue.text = value.toString()
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })

        // Pickers de tipo de espacio
        binding.cardHome.setOnClickListener { selectSpace("Hogar / Depto", binding.cardHome) }
        binding.cardBiz.setOnClickListener { selectSpace("Establecimiento", binding.cardBiz) }
        binding.cardSport.setOnClickListener { selectSpace("Espacio Deportivo", binding.cardSport) }

        editingId = intent.getStringExtra("eventId")
        if (editingId != null) {
            binding.tvScreenTitle.text = "Editar Evento"
            binding.btnCreate.text = "GUARDAR CAMBIOS ðŸ’¾"
            binding.etTitle.setText(intent.getStringExtra("title"))
            binding.etMatch.setText(intent.getStringExtra("match"))
            binding.etDate.setText(intent.getStringExtra("date"))
            binding.etLocation.setText(intent.getStringExtra("location"))
            binding.etFee.setText(intent.getStringExtra("fee"))
            val cupo = intent.getIntExtra("maxAttendees", 15).coerceIn(5, 30)
            binding.sbCupo.progress = cupo
            binding.tvCupoValue.text = cupo.toString()
            val sport = intent.getStringExtra("sport") ?: "FÃºtbol"
            val idx = sports.indexOf(sport)
            if (idx >= 0) binding.spinnerSport.setSelection(idx)
        }

        binding.btnCreate.setOnClickListener { saveEvent() }
    }

    private fun selectSpace(name: String, selected: LinearLayout) {
        selectedSpace = name
        listOf(binding.cardHome, binding.cardBiz, binding.cardSport).forEach {
            it.setBackgroundResource(
                if (it == selected) R.drawable.bg_picker_selected else R.drawable.bg_picker
            )
        }
    }

    private fun saveEvent() {
        val title = binding.etTitle.text.toString().trim()
        val match = binding.etMatch.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val sport = binding.spinnerSport.selectedItem.toString()
        val fee = binding.etFee.text.toString().trim().ifEmpty { "Gratis" }
        val cupo = binding.sbCupo.progress

        if (title.isEmpty() || match.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf<String, Any>(
            "title" to title,
            "match" to match,
            "date" to date,
            "location" to location,
            "spaceType" to selectedSpace,
            "fee" to fee,
            "maxAttendees" to cupo,
            "sport" to sport,
            "emoji" to Sports.emojiFor(sport),
            "hostId" to (auth.currentUser?.uid ?: ""),
            "timestamp" to System.currentTimeMillis()
        )

        val id = editingId
        if (id != null) {
            db.collection("events").document(id)
                .update(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Evento actualizado âœï¸", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        } else {
            data["taken"] = 0
            data["attendees"] = emptyList<String>()
            db.collection("events").add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Evento creado âœ…", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al crear evento", Toast.LENGTH_SHORT).show()
                }
        }
    }
}


