package com.example.justice4families.model

import android.net.Uri

data class EditProfileRequest(
    val username: String,
    val password: String,
    val newPassword: String,
    val picture: Uri
)
