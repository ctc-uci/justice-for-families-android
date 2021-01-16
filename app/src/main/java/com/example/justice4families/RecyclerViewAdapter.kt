package com.example.justice4families

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(private val postList: List<post_item>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = postList[position]
        holder.profilePic.setImageResource(currentItem.image_profile)
        holder.username.text = currentItem.username
        holder.subject.text = currentItem.subject
        holder.content.text = currentItem.content
        holder.like_counter.text = currentItem.like_count
        holder.share_counter.text = currentItem.share_count
        holder.comment_counter.text = currentItem.comment_count
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePic: ImageView = itemView.findViewById(R.id.profile_pic)
        val tags: TextView = itemView.findViewById(R.id.tags)
        val username: TextView = itemView.findViewById(R.id.username)
        val content: TextView = itemView.findViewById(R.id.content)
        val subject: TextView = itemView.findViewById(R.id.subject)
        val like_counter: TextView = itemView.findViewById(R.id.like_counter)
        val comment_counter: TextView = itemView.findViewById(R.id.comment_counter)
        val share_counter: TextView = itemView.findViewById(R.id.share_counter)
    }
}
