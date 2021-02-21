package com.example.justice4families

import java.text.SimpleDateFormat
import java.util.*


fun getDateTime(): String{
    return SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
}

object savedPreferences{
    var username = "Anonymous"
    fun setUserName(name: String){
        username = name
    }

    fun getUserName():String = username

}