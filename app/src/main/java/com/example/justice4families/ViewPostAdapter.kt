package com.example.justice4families

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.model.Comment
import com.example.justice4families.model.Post
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.hdodenhof.circleimageview.CircleImageView

class ViewPostAdapter (val context: Context, val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var items = emptyList<Any>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                val itemView = inflater.inflate(R.layout.view_post_card, parent, false)
                postViewHolder(context, itemView, bottomSheetBehavior)
            }
            else -> {
                val itemView = inflater.inflate(R.layout.comment_card, parent, false)
                commentsViewHolder(itemView)
            }
        }
    }

    fun setItems(items: List<Any>){
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = 1
        if (position == 0) viewType = 0
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == 0){
            val post : Post = items[position] as Post
            val postViewHolder = holder as postViewHolder
            postViewHolder.setPost(post)
        }
        else{
            val comment: Comment = items[position] as Comment
            val commentViewHolder = holder as commentsViewHolder
            commentViewHolder.setComments(comment)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
class commentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val name: TextView = itemView.findViewById(R.id.post_username)
    val timeStamp: TextView = itemView.findViewById(R.id.post_timestamp)
    val commentText: TextView = itemView.findViewById(R.id.post_content)

    fun setComments(comment: Comment){
        name.text = comment.name
        timeStamp.text = comment.timeStamp
        commentText.text = comment.commentText
    }
}

class postViewHolder(val context: Context, itemView: View, val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>?): RecyclerView.ViewHolder(itemView){
    private val username: TextView = itemView.findViewById(R.id.post_username)
    private val timeStamp: TextView = itemView.findViewById(R.id.post_timestamp)
    private val postContent: TextView = itemView.findViewById(R.id.post_content)
    val profileImage: CircleImageView = itemView.findViewById(R.id.profile_pic)
    private val topicHeadline: TextView = itemView.findViewById(R.id.topic_headline)
    private val like:TextView = itemView.findViewById(R.id.like_post)
    private val comment:TextView = itemView.findViewById(R.id.comment_post)
    private val join: Button = itemView.findViewById(R.id.join_button)
    private val joined: Button = itemView.findViewById(R.id.joined_button)
    private var likeClick = false

    fun setPost(post: Post){
        username.text = post.name
        timeStamp.text = post.timeStamp
        postContent.text = post.postText
        topicHeadline.text = post.topicHeadline
        like.setOnClickListener {
            likeClick = if(!likeClick){
                like.setTextColor(context.resources.getColor(R.color.purple_500))
                like.setTypeface(null, Typeface.BOLD)
                true
            } else{
                like.setTextColor(context.resources.getColor(R.color.gray))
                false
            }
        }

        comment.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        join.setOnClickListener {
            join.visibility = View.INVISIBLE
            joined.visibility = View.VISIBLE
        }

        joined.setOnClickListener {
            joined.visibility = View.INVISIBLE
            join.visibility = View.VISIBLE
        }

    }
}