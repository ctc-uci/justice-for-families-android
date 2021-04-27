package com.example.justice4families

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun getDateTime(): String{
    val current = LocalDateTime.now(ZoneId.of("GMT"))

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return current.format(formatter)
}

object savedPreferences{
    private lateinit var preferences: SharedPreferences
    private const val NAME = "LoggedInData"
    private const val MODE = Context.MODE_PRIVATE

    private val LOGGED_IN = Pair("is_login", false)
    private val USERNAME = Pair("username", "")

    fun init(context: Context) {
        preferences = context.getSharedPreferences("LoggedInData", Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var username: String
        get() = preferences.getString(USERNAME.first, USERNAME.second) ?: "Anonymous"
        set(value) {
            preferences.edit {
                it.putString(USERNAME.first, value)
            }
        }

    var loggedin: Boolean
        get() = preferences.getBoolean(LOGGED_IN.first, LOGGED_IN.second)
        set(value) {
            preferences.edit {
                it.putBoolean(LOGGED_IN.first, value)
            }
        }
}
