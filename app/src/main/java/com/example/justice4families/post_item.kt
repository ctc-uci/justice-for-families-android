package com.example.justice4families

import android.net.Uri


data class post_item(val imageProfile: Uri,
                     val subject: String,
                     val content: String,
                     val username: String,
                     val likeCount: String,
                     val commentCount: String,
                     val shareCount: String)
{}