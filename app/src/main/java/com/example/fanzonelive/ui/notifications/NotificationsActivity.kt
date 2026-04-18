package com.example.fanzonelive.ui.notifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }
        loadNotifications()
    }

    private fun loadNotifications() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("requests")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { docs ->
                binding.tvEmpty.visibility = if (docs.isEmpty)
                    android.view.View.VISIBLE else android.view.View.GONE
            }
    }
}