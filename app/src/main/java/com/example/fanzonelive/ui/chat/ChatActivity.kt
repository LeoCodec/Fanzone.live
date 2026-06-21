package com.example.fanzonelive.ui.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fanzonelive.R
import com.example.fanzonelive.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {

    private lateinit var tvChatTitle: TextView
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var messageAdapter: MessageAdapter
    private var eventId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        tvChatTitle = findViewById(R.id.tvChatTitle)
        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        val intentExtra = intent.getStringExtra("eventId")
        if (intentExtra != null) {
            eventId = intentExtra
        }

        messageAdapter = MessageAdapter(mutableListOf())
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = messageAdapter

        loadMessages()

        btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {
        if (eventId.isEmpty()) return
        
        db.collection("events").document(eventId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null) {
                    val messageList = mutableListOf<Message>()
                    for (doc in snapshot.documents) {
                        val msg = doc.toObject(Message::class.java)
                        if (msg != null) {
                            messageList.add(msg)
                        }
                    }
                    messageAdapter.updateMessages(messageList)
                    if (messageList.isNotEmpty()) {
                        rvMessages.scrollToPosition(messageList.size - 1)
                    }
                }
            }
    }

    private fun sendMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isEmpty() || eventId.isEmpty()) return
        
        val user = auth.currentUser
        var senderName = "User"
        
        if (user != null && user.email != null) {
            val email = user.email.toString()
            if (email.contains("@")) {
                senderName = email.substring(0, email.indexOf("@"))
            } else {
                senderName = email
            }
        }
        
        val msg = Message(
            senderId = user?.uid ?: "",
            senderName = senderName,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        db.collection("events").document(eventId).collection("messages").add(msg)
            .addOnSuccessListener {
                etMessage.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            }
    }
}