package com.example.justice4families

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


fun getDateTime(): String{
    val current = LocalDateTime.now(ZoneId.of("GMT"))

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return current.format(formatter)
}

fun getDiffDays(start: String, end: String): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val startDate: Date = sdf.parse(start)
    val endDate: Date = sdf.parse(end)

    var diff = endDate.time - startDate.time
    return (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)).toInt()
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
