package com.example.justice4families.model

import android.net.Uri

data class Post (
    val name: String?,
    val imageUri: Uri?,
    val topicHeadline: String?,
    val timeStamp: String?,
    val postText: String?
)