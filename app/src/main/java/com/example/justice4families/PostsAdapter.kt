package com.example.justice4families

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.model.Post


class PostsAdapter(val context: Context) : RecyclerView.Adapter<postViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var posts = emptyList<Post>()

    // Return the ViewHolder object
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postViewHolder{
        val itemView = inflater.inflate(R.layout.view_post_card, parent, false)
        return postViewHolder(context, itemView, null)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: postViewHolder, position: Int) {
        val post = posts[position]
        holder.setPost(post)
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ViewPostActivity::class.java)
            intent.putExtra("post", post)
            context.startActivity(intent)
        }
    }

    fun setPosts(posts: List<Post>){
        this.posts = posts.reversed()
        notifyDataSetChanged()
    }

}
