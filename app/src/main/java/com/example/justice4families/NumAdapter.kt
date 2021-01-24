package com.example.justice4families

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.model.Post


//class NumAdapter(val activity: MainActivity) : RecyclerView.Adapter<postViewHolder>() {
//    private val inflater: LayoutInflater = LayoutInflater.from(activity)
//    // Return the ViewHolder object
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postViewHolder{
//        val itemView = inflater.inflate(R.layout.view_post_card, parent, false)
//        return postViewHolder(activity.applicationContext, itemView, null)
//    }
//
//    override fun getItemCount(): Int {
//        return activity.numberList.size
//    }
//
//    override fun onBindViewHolder(holder: postViewHolder, position: Int) {
//        val post = Post("Anonymous", null, "Dummy Post", "1/21", activity.numberList[position])
//        holder.setPost(post)
//        holder.itemView.setOnClickListener{
//            val intent = Intent(activity, ViewPostActivity::class.java)
//            activity.startActivity(intent)
//        }
//    }
//
//}
