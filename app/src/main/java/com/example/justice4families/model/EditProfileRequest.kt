package com.example.justice4families.model

data class EditProfileRequest(
    val username: String,
    val password: String,
    val profilePicture: String?
)
