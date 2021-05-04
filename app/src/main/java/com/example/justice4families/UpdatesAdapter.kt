package com.example.justice4families

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.model.CommentUpdate

class UpdatesAdapter():RecyclerView.Adapter<UpdatesAdapter.UpdatesViewHolder>() {
    private var items = ArrayList<CommentUpdate>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatesViewHolder {
        Log.d("missed updates", items.toString())
        return UpdatesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.missed_message_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UpdatesViewHolder, position: Int) {
        var text = items[position].commentUsername

        if (items[position].postID != null) {
            text += " commented on your post!"
            holder.updateTime.text = items[position].commentDatePosted
        }

        holder.tvUpdateMsg.text = text
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class UpdatesViewHolder(v:View): RecyclerView.ViewHolder(v) {
        val tvUpdateMsg: TextView = v.findViewById(R.id.message)
        val updateTime: TextView = v.findViewById(R.id.time_stamp)
    }

    fun setUpdates(items: ArrayList<CommentUpdate>, username: String) {
        val lastCard = CommentUpdate (postID = null, postUsername = null, postTitle = null, postText = null, commentID = null,
        commentUsername = "See notifications for more", commentDatePosted = null, commentText = null)
        items.add(lastCard)

        val filteredItems = items.filter { it.commentUsername != username }
        this.items = filteredItems as ArrayList<CommentUpdate>

        notifyDataSetChanged()
    }

}