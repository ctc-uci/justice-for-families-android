package com.example.justice4families.model

data class EditProfileRequest(
    val _id: String?,
    val username: String,
    val password: String,
    val profilePicture: String?
)
