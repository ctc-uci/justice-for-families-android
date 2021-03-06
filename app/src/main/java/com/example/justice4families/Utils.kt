package com.example.justice4families

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.justice4families.data.ProfilePictureApi
import com.example.justice4families.model.EmailRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun getDateAndTime(dateIn: String, timeIn: String): String {
    val currDate = getDateTime().substring(0, 10)
    val currTime = getDateTime().substring(11, 19)

    val days = getDiffDays(dateIn, currDate)

    if ( (dateIn == currDate) && (currTime.substring(0, 5) == timeIn.substring(0, 5)) ) {
        return "Just now" // Same minute
    }
    else if ( (dateIn == currDate) && (currTime.substring(0, 2) == timeIn.substring(0, 2)) ) {
        val minutes = currTime.substring(3, 5).toInt() - timeIn.substring(3, 5).toInt()
        return "${minutes}m ago" // Same hour
    }
    else if (dateIn == currDate) {
        println("currTime: $currTime")
        println("timeIn: $timeIn")
        println("currTime hour for same day: ${currTime.substring(0, 2).toInt()}")
        println("timeIn hour for same day: ${timeIn.substring(0, 2).toInt()}")
        val hours = currTime.substring(0,2).toInt() - timeIn.substring(0,2).toInt()
        return "${hours}h ago" // Same day
    }
    else if (days < 7) {
        return "${days}d ago" // Same Week
    }
    else if (days/7 < 5) {
        return "${days/7}w ago" // Same month
    }
    return dateIn
}

private fun militaryToStandardTime(military_time: String): String {
    var hours = military_time.substring(0, 2)
    var min_sec = military_time.substring(2, 8)
    if (hours.toInt() == 0) {
        return "12$min_sec AM"
    }
    else if (hours.toInt() == 12) {
        return "12$min_sec PM"
    }
    else if (hours.toInt() > 12) {
        hours = (hours.toInt() - 12).toString()
        return "$hours$min_sec PM"
    }
    return "$military_time AM"
}

@RequiresApi(Build.VERSION_CODES.O)
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

fun parseUsername(username: String?): String {
    if (username != null) {
        val index = username.indexOf("@")
        val shortenName = username.substring(0,index)
        Log.d("username", shortenName)
        return shortenName
    }
    return ""
}

object savedPreferences{
    private lateinit var preferences: SharedPreferences
    private const val NAME = "LoggedInData"
    private const val MODE = Context.MODE_PRIVATE

    private val LOGGED_IN = Pair("is_login", false)
    private val USERNAME = Pair("username", "")
//    private val PROFILE_PIC = Pair("profilePic", "")

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

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

fun getProfilePic(username: String, callback: (url: String) -> Unit) {
    val request = EmailRequestBody(username)

    ProfilePictureApi().getProfilePicture(request)
        .enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Profile Picture", t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    callback(body.string().toString())
                } else {
                    Log.d("Profile Picture", "failed api call to get pfp")
                }
            }
        })
}