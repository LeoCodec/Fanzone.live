package com.example.fanzonelive.ui.host

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fanzonelive.R
import com.example.fanzonelive.model.Event

class MyEventAdapter(
    private val events: MutableList<Event>,
    private val onEdit: (Event) -> Unit,
    private val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<MyEventAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        val btnEdit: TextView = view.findViewById(R.id.btnEdit)
        val btnDelete: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_host_event, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = events[position]
        holder.tvEmoji.text = e.emoji.ifEmpty { "⚽" }
        holder.tvTitle.text = e.title
        holder.tvSub.text = "${e.date}  ·  📍 ${e.location}  ·  👥 ${e.maxAttendees}"
        holder.btnEdit.setOnClickListener { onEdit(e) }
        holder.btnDelete.setOnClickListener { onDelete(e) }
    }

    override fun getItemCount() = events.size

    fun update(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}
