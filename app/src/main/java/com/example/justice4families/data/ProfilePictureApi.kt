package com.example.justice4families.data

import com.example.justice4families.model.EmailRequestBody
import com.example.justice4families.model.LikedPostRequest
import com.example.justice4families.model.S3ContentType
import com.example.justice4families.model.S3URL
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.reflect.Type

interface ProfilePictureApi {
    @GET("s3Upload")
    fun getS3SignedURL(@Query("contentType") contentType: S3ContentType) : Call<S3URL>

    @PUT("")
    fun putImageToS3SignedURL(
        @Url s3UploadURL: String,
        @Body file: RequestBody
    ) : Call<ResponseBody>

    @POST("authentication/update/picture")
    fun updateProfilePicture(@Body request: RequestBody) : Call<ResponseBody>

    @POST("authentication/profilepic/get")
    fun getProfilePicture(@Body request: EmailRequestBody): Call<ResponseBody>

    class EnumConverterFactory : Converter.Factory() {
        override fun stringConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): Converter<Enum<*>, String>? =
            if (type is Class<*> && type.isEnum) {
                Converter { enum ->
                    try {
                        enum.javaClass.getField(enum.name)
                            .getAnnotation(SerializedName::class.java)?.value
                    } catch (e: Exception) {
                        null
                    } ?: enum.toString()
                }
            } else {
                null
            }
    }

    companion object {
        operator fun invoke(useBaseURL: Boolean = true): ProfilePictureApi {
            val builder = Retrofit.Builder()

            if (useBaseURL) {
                builder.baseUrl("https://j4f-backend.herokuapp.com/")
            }

            return builder.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(EnumConverterFactory())
                .build()
                .create(ProfilePictureApi::class.java)
        }
    }
}