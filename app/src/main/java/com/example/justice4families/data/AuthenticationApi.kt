package com.example.justice4families.data

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

    companion object{
        operator fun invoke(): AuthenticationApi {
            return Retrofit.Builder().baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthenticationApi::class.java)
        }
    }
}