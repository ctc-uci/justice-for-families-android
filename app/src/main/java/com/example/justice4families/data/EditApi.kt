package com.example.justice4families.data

import com.example.justice4families.model.EditProfileRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface EditApi {

    @POST("authentication/changePassword")
    fun editUserPassword(
        @Body request: EditProfileRequest
    ) : Call<ResponseBody>

    @POST("authentication/update/picture")
    fun editUserPicture(
        @Body request: EditProfileRequest
    ) : Call<ResponseBody>

    @POST("authentication/update/name")
    fun editUserName(
        @Body request: EditProfileRequest
    ) : Call<ResponseBody>

    companion object{
        operator fun invoke(): EditApi {
            return Retrofit.Builder().baseUrl("https://j4f-backend.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EditApi::class.java)
        }
    }
}