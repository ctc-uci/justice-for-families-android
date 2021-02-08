package com.example.justice4families.data

import com.example.justice4families.model.Post
import com.example.justice4families.model.Comment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.POST

interface PostApi {
    @GET("posts/username/{username}")
    fun getPostsByUsername(@Path("username") username: String) : Call<MutableList<Post>>

    @GET("posts/datePosted/{datePosted}")
    fun getPostsByDate(@Path("datePosted") datePosted: String) : Call<MutableList<Post>>

    @GET("posts/")
    fun getAllPosts(): Call<MutableList<Post>>

    @GET("comments/post/{postId}")
    fun getComments(@Path("postId") postId: String): Call<MutableList<Comment>>

    @POST("comments/{postId}/comments/create")
    fun postComment(@Path("postId") postId:String, @Body comment: Comment): Call<ResponseBody>

    companion object{
        operator fun invoke(): PostApi {
            return Retrofit.Builder().baseUrl("https://j4f-backend.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PostApi::class.java)
        }
    }
}