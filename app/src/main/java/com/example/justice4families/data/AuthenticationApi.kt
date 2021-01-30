package com.example.justice4families.data


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthenticationApi {

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<ResponseBody>

    companion object{
        operator fun invoke(): AuthenticationApi {
            return Retrofit.Builder().baseUrl("http://10.0.2.2:3000/authentication/") //192.168.1.157 10.0.2.2
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthenticationApi::class.java)
        }
    }
}