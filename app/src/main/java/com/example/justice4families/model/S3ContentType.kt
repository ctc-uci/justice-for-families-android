package com.example.justice4families.model

import com.google.gson.annotations.SerializedName

enum class S3ContentType(val mimeType: String) {
    @SerializedName("image/jpeg")
    IMG_JPEG("image/jpeg"),

    @SerializedName("image/png")
    IMG_PNG("image/png");

    companion object {
        fun getFromValue(value: String): S3ContentType? {
            return values().find { it.mimeType == value}
        }

        fun getAllValues(): Array<String> {
            return values().map { it.mimeType }.toTypedArray()
        }
    }
}
