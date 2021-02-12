package com.example.justice4families
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FeedPostApi {
    @Headers("Content-Type: application/json")
    @POST("posts/create")
    fun addPost(@Body postData: PostInfo): Call<ResponseBody>

    companion object{
        operator fun invoke(): FeedPostApi {
            return Retrofit.Builder().baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FeedPostApi::class.java)
        }
    }


}