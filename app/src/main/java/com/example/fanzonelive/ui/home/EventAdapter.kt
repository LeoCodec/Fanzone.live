package com.example.fanzonelive.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fanzonelive.R
import com.example.fanzonelive.model.Event
import com.example.fanzonelive.ui.detail.EventDetailActivity

class EventAdapter(private val events: MutableList<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvMatch: TextView = view.findViewById(R.id.tvMatch)
        val tvSport: TextView = view.findViewById(R.id.tvSport)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvCapacity: TextView = view.findViewById(R.id.tvCapacity)
        val pbCapacity: ProgressBar = view.findViewById(R.id.pbCapacity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.tvEmoji.text = event.emoji.ifEmpty { "⚽" }
        holder.tvTitle.text = event.title
        holder.tvMatch.text = event.match
        holder.tvSport.text = event.sport
        holder.tvDate.text = "📅 ${event.date}"
        holder.tvLocation.text = "📍 ${event.location}"

        val taken = event.taken.coerceAtMost(event.maxAttendees)
        holder.tvCapacity.text = "👥 $taken/${event.maxAttendees}"
        holder.pbCapacity.max = if (event.maxAttendees > 0) event.maxAttendees else 1
        holder.pbCapacity.progress = taken

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EventDetailActivity::class.java).apply {
                putExtra("eventId", event.id)
                putExtra("title", event.title)
                putExtra("match", event.match)
                putExtra("date", event.date)
                putExtra("location", event.location)
                putExtra("emoji", event.emoji)
                putExtra("sport", event.sport)
                putExtra("hostId", event.hostId)
                putExtra("fee", event.fee)
                putExtra("maxAttendees", event.maxAttendees)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = events.size

    fun updateData(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}
