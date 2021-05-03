package com.example.justice4families.model

data class CommentUpdate (
    val postID: String?,
    val postUsername: String?,
    val postTitle: String?,
    val postText: String?,
    val commentID: String?,
    val commentUsername: String?,
    val commentDatePosted: String?,
    val commentText: String?
)