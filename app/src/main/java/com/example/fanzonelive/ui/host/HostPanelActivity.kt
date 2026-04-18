package com.example.fanzonelive.ui.host

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityHostPanelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HostPanelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostPanelBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        loadRequests()
    }

    private fun loadRequests() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("events")
            .whereEqualTo("hostId", uid)
            .get()
            .addOnSuccessListener { events ->
                binding.tvEventCount.text = "${events.size()} eventos creados"
                binding.tvEmpty.visibility = if (events.isEmpty)
                    android.view.View.VISIBLE else android.view.View.GONE
            }
    }
}