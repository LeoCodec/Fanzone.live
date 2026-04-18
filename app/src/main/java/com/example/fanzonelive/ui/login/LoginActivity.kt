package com.example.fanzonelive.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fanzonelive.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) { goToHome(); return }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass  = binding.etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { goToHome() }
                .addOnFailureListener { Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show() }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, com.example.fanzonelive.ui.register.RegisterActivity::class.java))
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, com.example.fanzonelive.ui.home.HomeActivity::class.java))
        finish()
    }
}
