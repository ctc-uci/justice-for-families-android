package com.example.justice4families

import com.google.gson.annotations.SerializedName

data class PostInfo(
    val username: String,
    val title: String,
    val text: String,
    val tags: List<String>?,
    val anonymous: Boolean?,
    val numComments: Int
)


