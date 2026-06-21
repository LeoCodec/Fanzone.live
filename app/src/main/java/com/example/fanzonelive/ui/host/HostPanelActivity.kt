package com.example.fanzonelive.ui.host

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fanzonelive.databinding.ActivityHostPanelBinding
import com.example.fanzonelive.model.Event
import com.example.fanzonelive.ui.create.CreateEventActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HostPanelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostPanelBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val myEvents = mutableListOf<Event>()
    private val requests = mutableListOf<JoinRequest>()
    private lateinit var eventAdapter: MyEventAdapter
    private lateinit var requestAdapter: RequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }

        eventAdapter = MyEventAdapter(myEvents,
            onEdit = { e -> editEvent(e) },
            onDelete = { e -> confirmDelete(e) }
        )
        binding.rvMyEvents.layoutManager = LinearLayoutManager(this)
        binding.rvMyEvents.adapter = eventAdapter

        requestAdapter = RequestAdapter(requests,
            onAccept = { r -> resolveRequest(r, "accepted") },
            onReject = { r -> resolveRequest(r, "rejected") }
        )
        binding.rvRequests.layoutManager = LinearLayoutManager(this)
        binding.rvRequests.adapter = requestAdapter
    }

    override fun onResume() {
        super.onResume()
        loadMyEvents()
        loadRequests()
    }

    // ---------- READ ----------
    private fun loadMyEvents() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("events").whereEqualTo("hostId", uid).get()
            .addOnSuccessListener { docs ->
                val list = docs.map { d ->
                    Event(
                        id = d.id,
                        title = d.getString("title") ?: "",
                        match = d.getString("match") ?: "",
                        date = d.getString("date") ?: "",
                        location = d.getString("location") ?: "",
                        hostId = d.getString("hostId") ?: "",
                        maxAttendees = d.getLong("maxAttendees")?.toInt() ?: 10,
                        emoji = d.getString("emoji") ?: "⚽",
                        sport = d.getString("sport") ?: "Fútbol"
                    )
                }
                eventAdapter.update(list)
                binding.tvEventCount.text = "${list.size} eventos creados"
                binding.tvEmptyEvents.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    private fun loadRequests() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("requests")
            .whereEqualTo("hostId", uid)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { docs ->
                val list = docs.map { d ->
                    JoinRequest(
                        id = d.id,
                        userEmail = d.getString("userEmail") ?: "Usuario",
                        eventTitle = d.getString("eventTitle") ?: "Evento",
                        userId = d.getString("userId") ?: "",
                        eventId = d.getString("eventId") ?: ""
                    )
                }
                requestAdapter.update(list)
                binding.tvEmptyRequests.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    // ---------- UPDATE (editar evento) ----------
    private fun editEvent(e: Event) {
        val i = Intent(this, CreateEventActivity::class.java).apply {
            putExtra("eventId", e.id)
            putExtra("title", e.title)
            putExtra("match", e.match)
            putExtra("date", e.date)
            putExtra("location", e.location)
            putExtra("sport", e.sport)
            putExtra("maxAttendees", e.maxAttendees)
        }
        startActivity(i)
    }

    // ---------- UPDATE (aceptar/rechazar solicitud) ----------
    private fun resolveRequest(r: JoinRequest, status: String) {
        db.collection("requests").document(r.id)
            .update("status", status)
            .addOnSuccessListener {
                if (status == "accepted" && r.eventId.isNotEmpty()) {
                    db.collection("events").document(r.eventId)
                        .update("attendees", FieldValue.arrayUnion(r.userId))
                }
                val msg = if (status == "accepted") "Solicitud aceptada ✅" else "Solicitud rechazada ❌"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                loadRequests()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar la solicitud", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------- DELETE ----------
    private fun confirmDelete(e: Event) {
        AlertDialog.Builder(this)
            .setTitle("Borrar evento")
            .setMessage("¿Seguro que quieres eliminar \"${e.title}\"?")
            .setPositiveButton("Borrar") { _, _ -> deleteEvent(e) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteEvent(e: Event) {
        db.collection("events").document(e.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Evento eliminado 🗑️", Toast.LENGTH_SHORT).show()
                loadMyEvents()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }
}
