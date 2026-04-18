package com.example.fanzonelive.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityProfileBinding
import com.example.fanzonelive.ui.login.LoginActivity
import com.example.fanzonelive.ui.host.HostPanelActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        user?.let {
            binding.tvUserName.text = it.displayName ?: "Fan deportivo"
            binding.tvUserEmail.text = it.email ?: ""
        }

        binding.tvLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.tvHostPanel.setOnClickListener {
            startActivity(Intent(this, HostPanelActivity::class.java))
        }
    }
}