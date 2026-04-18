package com.example.fanzonelive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.fanzonelive.ui.login.LoginActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
