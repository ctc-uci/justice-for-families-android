package com.example.justice4families

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.CommentUpdate
import com.example.justice4families.model.Post
import com.example.justice4families.model.PostRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class UpdatesAdapter(val context: Context):RecyclerView.Adapter<UpdatesAdapter.UpdatesViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = ArrayList<CommentUpdate>()
    private var post : Post? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatesViewHolder {
        val itemView = inflater.inflate(R.layout.missed_message_card, parent, false)

        if (context is NotificationActivity) {
            val card: CardView = itemView.findViewById(R.id.comment_card)
            card.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        }

        return UpdatesViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: UpdatesViewHolder, position: Int) {
        val commentUpdate = items[position]

        if (commentUpdate.postID != null) {
            val text = "@${parseUsername(commentUpdate.commentUsername)} commented on your post!"
            holder.tvUpdateMsg.text = text

            getPost(commentUpdate.postID)

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ViewPostActivity::class.java)
                intent.putExtra("post", post)
                context.startActivity(intent)
            }
        }
        else {
            holder.tvUpdateMsg.text = commentUpdate.commentUsername

            if (context is MainActivity) {
                holder.itemView.setOnClickListener{
                    val intent = Intent(context, NotificationActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }

        if (commentUpdate.commentDatePosted != null) {
            val dateFromBackend = commentUpdate.commentDatePosted!!.substring(0, 10)
            val timeFromBackend = commentUpdate.commentDatePosted.substring(11, 19)
            holder.updateTime.text = getDateAndTime(dateFromBackend, timeFromBackend)
        }
        else {
            holder.updateTime.text = ""
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class UpdatesViewHolder(v:View): RecyclerView.ViewHolder(v) {
        val tvUpdateMsg: TextView = v.findViewById(R.id.message)
        val updateTime: TextView = v.findViewById(R.id.time_stamp)
    }

    fun setUpdates(items: ArrayList<CommentUpdate>, username: String) {

        val filteredItems = items.filter { it.commentUsername != username }.reversed().toMutableList()

        if (context is MainActivity) {
            val lastCard = CommentUpdate (postID = null, postUsername = null, postTitle = null, postText = null, commentID = null,
                commentUsername = "Want to see more updates? Visit the notifications tab.", commentDatePosted = null, commentText = null)

            filteredItems.add(lastCard)
        }

        this.items = filteredItems as ArrayList<CommentUpdate>
        notifyDataSetChanged()
    }

    private fun getPost(postId: String) {
        PostApi().getPostById(PostRequest(postId))
            .enqueue(object : Callback<Post> {
                override fun onFailure(call: Call<Post>, t: Throwable) {
                    Log.d("missed updates post", t.message.toString())
                }

                override fun onResponse(
                    call: Call<Post>,
                    response: Response<Post>
                ) {
                    if(response.isSuccessful) {
                        Log.d("missed updates post", "api call successful")
                        post = response.body()!!
                        Log.d("missed updates post api", post.toString())

                    }
                    else {
                        Log.d("missed updates post", "api unsuccessful")
                    }
                }
            })
    }

}