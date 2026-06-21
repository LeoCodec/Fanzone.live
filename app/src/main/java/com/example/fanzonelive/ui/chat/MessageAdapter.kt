package com.example.fanzonelive.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fanzonelive.R
import com.example.fanzonelive.model.Message

class MessageAdapter(private var messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSender: TextView = itemView.findViewById(R.id.tvMessageSender)
        private val tvText: TextView = itemView.findViewById(R.id.tvMessageText)

        fun bind(message: Message) {
            tvSender.text = message.senderName
            tvText.text = message.text
        }
    }
}