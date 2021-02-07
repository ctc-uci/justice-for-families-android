package com.example.justice4families.model

import android.net.Uri

//data class Post (
//    val id: String?,
//    val name: String?,
//    val imageUri: Uri?,
//    val topicHeadline: String?,
//    val timeStamp: String?,
//    val postText: String?
//)

data class Post (
    val _id: String?,
    val username: String?,
    val tags: List<String>?,
    val datePosted: String?,
    val numComments: Int?,
    val anonymous: Boolean?,
    val title: String?,
    val text: String?,
    val media: String?
)