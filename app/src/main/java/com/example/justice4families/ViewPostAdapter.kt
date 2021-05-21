package com.example.justice4families

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.*
import com.example.justice4families.profile.UserProfileActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class ViewPostAdapter(val context: Context, val comment_textfield: EditText): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var items = emptyList<Any>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                val itemView = inflater.inflate(R.layout.view_post_card, parent, false)
                postViewHolder(context, itemView, comment_textfield)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("view post adapter bind", items[position].toString())
        //holder.setIsRecyclable(false)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun setComments(comment: Comment){
        name.text = parseUsername(comment.username)

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

class postViewHolder(val context: Context,
                     itemView: View,
                     val comment_textfield: EditText?) : RecyclerView.ViewHolder(itemView) {
    private val username: TextView = itemView.findViewById(R.id.post_username)
    private val timeStamp: TextView = itemView.findViewById(R.id.post_timestamp)
    private val postContent: TextView = itemView.findViewById(R.id.post_content)
    private val profileImage: CircleImageView = itemView.findViewById(R.id.profile_pic)
    private val topicHeadline: TextView = itemView.findViewById(R.id.topic_headline)
    private val like: TextView = itemView.findViewById(R.id.like_post)
    private val comment: TextView = itemView.findViewById(R.id.comment_post)
    private val blueThumb: ImageView = itemView.findViewById(R.id.blue_thumb)
    private val grayThumb: ImageView = itemView.findViewById(R.id.gray_thumb)
    private val likeCount: TextView = itemView.findViewById(R.id.like_num)
    private val commentCount: TextView = itemView.findViewById(R.id.comment_num)
    private val actionBar: ConstraintLayout = itemView.findViewById(R.id.action_bar)
    private var likes = 0
    private var likeHandler = Handler()
    private lateinit var runnable: Runnable
    private var setup = true
    private var originalLiked: Boolean by Delegates.observable(false) { property, oldValue, newValue ->
        currentlyLiked = newValue
    }
    private var currentlyLiked: Boolean by Delegates.observable(false) { property, oldValue, liked ->
        if (!setup) {
            if (liked) {
                like.setTextColor(context.resources.getColor(R.color.purple_500))
                grayThumb.visibility = View.INVISIBLE
                blueThumb.visibility = View.VISIBLE
                likes += 1
                startHandler(runnable)
            } else {
                like.setTextColor(context.resources.getColor(R.color.gray))
                blueThumb.visibility = View.INVISIBLE
                grayThumb.visibility = View.VISIBLE
                likes -= 1
                startHandler(runnable)
            }
        }
        Log.d("likes observable", "$liked $likes")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setPost(post: Post) {
        username.text = if (post.anonymous!!) "Anonymous" else parseUsername(post.username)

        if (context is ViewPostActivity) {
            checkUserLiked(post, savedPreferences.username)
        }

        if (post.anonymous == false) {
            getProfilePic(post.username!!, callback = {
                Log.d("Setting profile pic", it)
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.profileplaceholder)
                    .error(R.drawable.profileplaceholder)
                    .resize(50, 50)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(profileImage)
            })
        } else {
            profileImage.setImageResource(R.drawable.profileplaceholder)
        }
        else {
            profileImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.profileplaceholder))
        }

        profileImage.setOnClickListener {
            if (context !is UserProfileActivity && username.text != "Anonymous") {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra("post_username", post.username)
                context.startActivity(intent)
            }
            else {
                profileImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.profileplaceholder))
            }
        }

        if (post.datePosted?.length!! > 18) {
            val dateFromBackend = post.datePosted.substring(0, 10)
            val timeFromBackend = post.datePosted.substring(11, 19)
            timeStamp.text = getDateAndTime(dateFromBackend, timeFromBackend)
        } else {
            timeStamp.text = post.datePosted
        }

        if (context is MainActivity) {
            topicHeadline.ellipsize = TextUtils.TruncateAt.END
            topicHeadline.maxLines = 1
            postContent.maxLines = 3
            postContent.ellipsize = TextUtils.TruncateAt.END
        }

        postContent.text = post.text

        topicHeadline.text = post.title
        setTag(itemView, post.tags)
        likes = post.numLikes ?: 0
        likeCount.text = likes.toString()

        like.setTextColor(context.resources.getColor(R.color.gray))
        like.setTypeface(null, Typeface.BOLD)
        blueThumb.visibility = View.INVISIBLE
        grayThumb.visibility = View.VISIBLE

        runnable = Runnable {
            if (currentlyLiked != originalLiked) {
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
                setup = true
            }
        }

        commentCount.text = String.format(context.resources.getString(R.string.num_comments), post.numComments)

        if (context is ViewPostActivity) actionBar.visibility = View.VISIBLE

        //fix this later
        comment.setOnClickListener {
            comment_textfield?.requestFocus()
        }
    }

    private fun startHandler(runnable: Runnable) {
        Log.d("runnable", "runnable removed")
        likeHandler.removeCallbacks(runnable)
        Log.d("runnable", "runnable starting")
        likeHandler.postDelayed(runnable, 2000)
    }

    private fun checkUserLiked(post: Post, username: String) {
        PostApi().hasUserLiked(post._id!!, username)
            .enqueue(object : Callback<LikeResponse> {
                override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    Log.d("likes", t.message.toString())
                }

                override fun onResponse(
                    call: Call<LikeResponse>,
                    response: Response<LikeResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("likes", response.body().toString())
                        originalLiked = response.body()!!.hasLiked!!
                        setup = false
                        Log.d("likes in response", originalLiked.toString())
                        if (originalLiked) {
                            like.setTextColor(context.resources.getColor(R.color.purple_500))
                            grayThumb.visibility = View.INVISIBLE
                            blueThumb.visibility = View.VISIBLE
                        }
                        handleLikeListener(post)
                    }
                }
            })
    }

    private fun handleLikeListener(post: Post) {
        like.setOnClickListener {
            currentlyLiked = !currentlyLiked
            likeCount.text = likes.toString()
            post.numLikes = likes
        }
    }

    private fun likePost(postId: String, username: String) {
        PostApi().likePost(Like(postId = postId, username = username))
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) Log.d("api_call", "$username liked $postId")
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
            } else if (tag2.text == context.getString(R.string.post_tag_placeholder) || tag2.text == tag1.text) {
                    tag2.visibility = View.GONE
            }
        }
    }

}
