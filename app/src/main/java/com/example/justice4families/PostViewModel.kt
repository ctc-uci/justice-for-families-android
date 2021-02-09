package com.example.justice4families

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.Comment
import com.example.justice4families.model.Post
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel(){

    private val items = mutableListOf<Any>()
    private lateinit var post: Post

    private val _postItems:MutableLiveData<List<Any>> = MutableLiveData()


    fun addComment(comment: Comment){
        items.add(comment)
        _postItems.value = items
        PostApi().postComment(post._id!!, comment)
            .enqueue(object : Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) println("successfully added a comment")
                }

            })

    }

    private fun fetchComments(post:Post){
        PostApi().getComments(post._id!!)
            .enqueue(object : Callback<MutableList<Comment>>{
                override fun onFailure(call: Call<MutableList<Comment>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<MutableList<Comment>>,
                    response: Response<MutableList<Comment>>
                ) {
                    if(response.isSuccessful){
                        items.add(post)
                        response.body()?.let { comments->
                            for(comment in comments) {
                                println(comment)
                                items.add(comment)
                            }
                        }
                        _postItems.value = items
                    }
                }
            })

    }

    fun setPost(post: Post){
        this.post = post
        fetchComments(post)
    }

    fun getPost() =_postItems as LiveData<List<Any>>
}