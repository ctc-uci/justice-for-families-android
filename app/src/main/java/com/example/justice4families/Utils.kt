package com.example.justice4families

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun getDateTime(): String{
    val current = LocalDateTime.now(ZoneId.of("GMT"))

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return current.format(formatter)
}

object savedPreferences{
    var username = "Anonymous"
    fun setUserName(name: String){
        username = name
    }

    fun getUserName():String = username
}