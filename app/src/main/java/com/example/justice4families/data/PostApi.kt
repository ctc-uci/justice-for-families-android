package com.example.justice4families.data

import com.example.justice4families.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PostApi {
    @GET("posts/username/{username}")
    fun getPostsByUsername(@Path("username") username: String) : Call<MutableList<Post>>

    @GET("posts/datePosted/{datePosted}")
    fun getPostsByDate(@Path("datePosted") datePosted: String) : Call<MutableList<Post>>

    @GET("posts/")
    fun getAllPosts(): Call<MutableList<Post>>

    @GET("comments/post/{postId}")
    fun getComments(@Path("postId") postId: String): Call<MutableList<Comment>>

    @GET("posts/{postId}/user/{username}/hasLiked")
    fun hasUserLiked(@Path("postId") postId: String, @Path("username") username: String) : Call<LikeResponse>

    @POST("posts/id")
    fun getPostById(@Body postRequest: PostRequest): Call<Post>

    @POST("comments/{postId}/comments/create")
    fun postComment(@Path("postId") postId:String, @Body comment: Comment): Call<ResponseBody>

    @POST("posts/create")
    fun addPost(@Body postData: Post): Call<ResponseBody>

    @POST("likes/like")
    fun likePost(@Body postLiked: Like): Call<ResponseBody>

    @POST("likes/unlike")
    fun unlikePost(@Body postLiked: Like): Call<ResponseBody>

    @POST("activity")
    fun getMissedActivity(@Body request: UpdatesRequest): Call<Update>

    @GET("tags")
    fun getAllTags(): Call<MutableList<String>>

    @POST("/likes/byUser")
    fun getLikesByUser(@Body request: LikedPostRequest): Call<MutableList<Post>>

    companion object{
        operator fun invoke(): PostApi {
            return Retrofit.Builder().baseUrl("https://j4f-backend.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PostApi::class.java)
        }
    }
}