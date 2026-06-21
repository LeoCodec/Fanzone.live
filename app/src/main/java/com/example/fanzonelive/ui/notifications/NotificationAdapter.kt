package com.example.fanzonelive.ui.notifications

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fanzonelive.R

data class NotifItem(val eventTitle: String, val status: String)

class NotificationAdapter(private val items: MutableList<NotifItem>) :
    RecyclerView.Adapter<NotificationAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvNotifTitle)
        val tvStatus: TextView = view.findViewById(R.id.tvNotifStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = items[position]
        holder.tvTitle.text = n.eventTitle
        when (n.status) {
            "accepted" -> {
                holder.tvStatus.text = "✅ Aceptada — ¡ya puedes asistir!"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
            }
            "rejected" -> {
                holder.tvStatus.text = "❌ Rechazada por el anfitrión"
                holder.tvStatus.setTextColor(Color.parseColor("#E53935"))
            }
            else -> {
                holder.tvStatus.text = "⏳ Pendiente de aprobación"
                holder.tvStatus.setTextColor(Color.parseColor("#FFB300"))
            }
        }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<NotifItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
