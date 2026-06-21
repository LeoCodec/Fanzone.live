package com.example.fanzonelive.ui.host

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fanzonelive.R

data class JoinRequest(
    val id: String,
    val userEmail: String,
    val eventTitle: String,
    val userId: String,
    val eventId: String
)

class RequestAdapter(
    private val items: MutableList<JoinRequest>,
    private val onAccept: (JoinRequest) -> Unit,
    private val onReject: (JoinRequest) -> Unit
) : RecyclerView.Adapter<RequestAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvUser: TextView = view.findViewById(R.id.tvUser)
        val tvEvent: TextView = view.findViewById(R.id.tvEvent)
        val btnAccept: TextView = view.findViewById(R.id.btnAccept)
        val btnReject: TextView = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]
        holder.tvUser.text = r.userEmail.ifEmpty { "Usuario" }
        holder.tvEvent.text = "Quiere unirse a: ${r.eventTitle}"
        holder.btnAccept.setOnClickListener { onAccept(r) }
        holder.btnReject.setOnClickListener { onReject(r) }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<JoinRequest>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
