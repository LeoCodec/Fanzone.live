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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val e = events[position]
        holder.tvEmoji.text = e.emoji.ifEmpty { "âš½" }
        holder.tvTitle.text = e.title
        holder.tvMatch.text = e.match
        holder.tvSport.text = e.sport
        holder.tvDate.text = "ðŸ“… " + e.date
        holder.tvLocation.text = "ðŸ“ " + e.location
        val taken = e.taken.coerceAtMost(e.maxAttendees)
        holder.tvCapacity.text = "ðŸ‘¥ " + taken.toString() + "/" + e.maxAttendees.toString()
        holder.pbCapacity.max = if (e.maxAttendees > 0) e.maxAttendees else 1
        holder.pbCapacity.progress = taken
        holder.itemView.setOnClickListener {
            val i = Intent(holder.itemView.context, EventDetailActivity::class.java).apply {
                putExtra("eventId", e.id); putExtra("title", e.title)
                putExtra("match", e.match); putExtra("date", e.date)
                putExtra("location", e.location); putExtra("emoji", e.emoji)
                putExtra("sport", e.sport); putExtra("hostId", e.hostId)
                putExtra("maxAttendees", e.maxAttendees); putExtra("fee", e.fee)
            }
            holder.itemView.context.startActivity(i)
        }
    }
    override fun getItemCount() = events.size
    fun updateData(newEvents: List<Event>) { events.clear(); events.addAll(newEvents); notifyDataSetChanged() }
    fun updateList(newEvents: List<Event>) = updateData(newEvents)
}