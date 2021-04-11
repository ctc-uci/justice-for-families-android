package com.example.justice4families

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UpdatesAdapter(val items:ArrayList<String>):RecyclerView.Adapter<UpdatesAdapter.UpdatesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatesViewHolder {
        return UpdatesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.missed_message_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UpdatesViewHolder, position: Int) {
        holder.tvUpdateMsg.text=items[position]
    }

    override fun getItemCount(): Int {
        return items.size

    }

    class UpdatesViewHolder(v:View): RecyclerView.ViewHolder(v) {
        val tvUpdateMsg: TextView =v.findViewById(R.id.message)
    }
}