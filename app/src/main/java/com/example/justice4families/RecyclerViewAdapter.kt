package com.example.justice4families

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(private val postList: List<post_item>, val activity: MainActivity) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder (holder: ViewHolder, position: Int) {
        val currentItem = postList[position]
        holder.profilePic.setImageURI(currentItem.imageProfile)
        holder.username.text = currentItem.username
        holder.subject.text = currentItem.subject
        holder.content.text = currentItem.content
        holder.likeCounter.text = currentItem.likeCount
        holder.shareCounter.text = currentItem.shareCount
        holder.commentCounter.text = currentItem.commentCount
        holder.itemView.setOnClickListener{
            val intent = Intent(activity, ViewPostActivity::class.java)
            activity.startActivity(intent)
        };


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
        val likeCounter: TextView = itemView.findViewById(R.id.like_counter)
        val commentCounter: TextView = itemView.findViewById(R.id.comment_counter)
        val shareCounter: TextView = itemView.findViewById(R.id.share_counter)
    }
}
