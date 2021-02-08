package com.example.justice4families.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
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
): Parcelable