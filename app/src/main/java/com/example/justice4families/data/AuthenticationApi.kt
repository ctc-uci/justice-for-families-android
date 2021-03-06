package com.example.justice4families.data

import com.example.justice4families.model.LoginRequest
import com.example.justice4families.model.SignUpRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("authentication/register")
    fun registerUser(
        @Body request: SignUpRequest
    ) : Call<ResponseBody>

    @POST("authentication/login")
    fun loginUser(
        @Body request: LoginRequest
    ) : Call<ResponseBody>

    companion object{
        operator fun invoke(): AuthenticationApi {
            return Retrofit.Builder().baseUrl("https://j4f-backend.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthenticationApi::class.java)
        }
    }
}