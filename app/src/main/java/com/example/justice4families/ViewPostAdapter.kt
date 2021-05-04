package com.example.justice4families

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
//import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.Comment
import com.example.justice4families.model.Like
import com.example.justice4families.model.Post
import com.example.justice4families.profile.UserProfileActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewPostAdapter (val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var items = emptyList<Any>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                val itemView = inflater.inflate(R.layout.view_post_card, parent, false)
                //postViewHolder(context, itemView, bottomSheetBehavior)
                postViewHolder(context, itemView)
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
        name.text = comment.username

        if (comment.datePosted?.length!! > 18) {
            val dateFromBackend = comment.datePosted.substring(0, 10)
            val timeFromBackend = comment.datePosted.substring(11, 19)
            timeStamp.text = getDateAndTime(dateFromBackend, timeFromBackend)
        }
        else {
            timeStamp.text = comment.datePosted
        }

        commentText.text = comment.text
    }
}

class postViewHolder(val context: Context, itemView: View): RecyclerView.ViewHolder(itemView){
    private val username: TextView = itemView.findViewById(R.id.post_username)
    private val timeStamp: TextView = itemView.findViewById(R.id.post_timestamp)
    private val postContent: TextView = itemView.findViewById(R.id.post_content)
    val profileImage: CircleImageView = itemView.findViewById(R.id.profile_pic)
    private val topicHeadline: TextView = itemView.findViewById(R.id.topic_headline)
    private val like:TextView = itemView.findViewById(R.id.like_post)
    private val comment:TextView = itemView.findViewById(R.id.comment_post)
    private val blueThumb: ImageView = itemView.findViewById(R.id.blue_thumb)
    private val grayThumb: ImageView = itemView.findViewById(R.id.gray_thumb)
    private val likeCount: TextView = itemView.findViewById(R.id.like_num)
    private val commentCount: TextView = itemView.findViewById(R.id.comment_num)
    private val actionBar: ConstraintLayout = itemView.findViewById(R.id.action_bar)
    private var likes = 0
    var likeHandler = Handler()

    fun setPost(post: Post) {
        username.text = if (post.anonymous!!) "Anonymous" else post.username

        if (post.datePosted?.length!! > 18) {
            val dateFromBackend = post.datePosted.substring(0, 10)
            val timeFromBackend = post.datePosted.substring(11, 19)
            timeStamp.text = getDateAndTime(dateFromBackend, timeFromBackend)
        }
        else {
            timeStamp.text = post.datePosted
        }

        postContent.text = post.text
        topicHeadline.text = post.title
        setTag(itemView, post.tags)
        likes = post.numLikes ?: 0
        likeCount.text = likes.toString()

        var originalLiked = false // TODO: get from list of user's liked posts
        var postLiked = originalLiked

        val runnable = Runnable {
            if (postLiked != originalLiked) {
                originalLiked = if (originalLiked) {
                    unlikePost(post._id!!, savedPreferences.username)
                    Log.d("runnable", "post like was changed, post originally liked")
                    false
                }
                else {
                    likePost(post._id!!, savedPreferences.username)
                    Log.d("runnable", "post like was changed, post NOT originally liked")
                    true
                }
            }
        }

        if (originalLiked){
            like.setTextColor(context.resources.getColor(R.color.purple_500))
            like.setTypeface(null, Typeface.BOLD)
            grayThumb.visibility = View.INVISIBLE
            blueThumb.visibility = View.VISIBLE
        }
        else {
            like.setTextColor(context.resources.getColor(R.color.gray))
            blueThumb.visibility = View.INVISIBLE
            grayThumb.visibility = View.VISIBLE
        }

        commentCount.text = String.format(context.resources.getString(R.string.num_comments), post.numComments)

        if(context is ViewPostActivity) actionBar.visibility = View.VISIBLE

        like.setOnClickListener {
            postLiked = if(!postLiked){
                like.setTextColor(context.resources.getColor(R.color.purple_500))
                like.setTypeface(null, Typeface.BOLD)
                grayThumb.visibility = View.INVISIBLE
                blueThumb.visibility = View.VISIBLE
                likes += 1
                startHandler(runnable)
                true
            } else{
                like.setTextColor(context.resources.getColor(R.color.gray))
                blueThumb.visibility = View.INVISIBLE
                grayThumb.visibility = View.VISIBLE
                likes -= 1
                startHandler(runnable)
                false
            }
            likeCount.text = likes.toString()
            post.numLikes = likes
        }

        //fix this later
        comment.setOnClickListener {
            //bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }

        if(context !is UserProfileActivity){
            profileImage.setOnClickListener{
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("post_username", post.username)
                context.startActivity(intent)
            }
        }

    }

    private fun startHandler(runnable: Runnable) {
        Log.d("runnable", "runnable removed")
        likeHandler.removeCallbacks(runnable)
        Log.d("runnable", "runnable starting")
        likeHandler.postDelayed(runnable, 2000)
    }

    private fun likePost(postId: String, username: String){
        PostApi().likePost(Like(postId = postId, username = username))
            .enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) Log.d("api_call", "$username liked $postId")
                }

            })

    }

    private fun unlikePost(postId: String, username: String) {
        PostApi().unlikePost(Like(postId = postId, username = username))
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) Log.d("api_call", "$username unliked $postId")
                }

            })
    }

    private fun setTag(view: View, tagsList: List<String>?) {
        val tag1: TextView = view.findViewById(R.id.post_tag1)
        val tag2: TextView = view.findViewById(R.id.post_tag2)

        if (tagsList != null) {
            tag1.text = tagsList[0].removePrefix("#")

            if (tagsList.size > 1) {
                tag2.text = tagsList[1].removePrefix("#")
            }
            else {
                tag2.visibility = View.GONE
            }

        }
    }
}

private fun getDateAndTime(dateIn: String, timeIn: String): String {
    val currDate = getDateTime().substring(0, 10)
    val currTime = getDateTime().substring(11, 19)

    val days = getDiffDays(dateIn, currDate)

    if ( (dateIn == currDate) && (currTime.substring(0, 5) == timeIn.substring(0, 5)) ) {
        return "Just now" // Same minute
    }
    else if ( (dateIn == currDate) && (currTime.substring(0, 2) == timeIn.substring(0, 2)) ) {
        val minutes = currTime.substring(3, 5).toInt() - timeIn.substring(3, 5).toInt()
        return "${minutes}m ago" // Same hour
    }
    else if (dateIn == currDate) {
        println("currTime: $currTime")
        println("timeIn: $timeIn")
        println("currTime hour for same day: ${currTime.substring(0, 2).toInt()}")
        println("timeIn hour for same day: ${timeIn.substring(0, 2).toInt()}")
        val hours = currTime.substring(0,2).toInt() - timeIn.substring(0,2).toInt()
        return "${hours}h ago" // Same day
    }
    else if (days < 7) {
        return "${days}d ago" // Same Week
    }
    else if (days/7 < 5) {
        return "${days/7}w ago" // Same month
    }
    return dateIn
}

private fun militaryToStandardTime(military_time: String): String {
    var hours = military_time.substring(0, 2)
    var min_sec = military_time.substring(2, 8)
    if (hours.toInt() == 0) {
        return "12$min_sec AM"
    }
    else if (hours.toInt() == 12) {
        return "12$min_sec PM"
    }
    else if (hours.toInt() > 12) {
        hours = (hours.toInt() - 12).toString()
        return "$hours$min_sec PM"
    }
    return "$military_time AM"
}