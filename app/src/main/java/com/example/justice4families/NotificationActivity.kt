package com.example.justice4families

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.justice4families.data.PostApi
import com.example.justice4families.model.Update
import com.example.justice4families.model.UpdatesRequest
import com.example.justice4families.profile.UserProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class NotificationActivity : AppCompatActivity() {

    lateinit var adapter: UpdatesAdapter
    lateinit var layoutManager : LinearLayoutManager
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        layoutManager = LinearLayoutManager(this)
        var notificationRecyclerView = findViewById<RecyclerView>(R.id.recyclerViewNotifications)
        notificationRecyclerView.isNestedScrollingEnabled = false
        adapter = UpdatesAdapter(this)
        notificationRecyclerView.adapter = adapter
        notificationRecyclerView.layoutManager = layoutManager
        getNotifications(savedPreferences.username)


        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_addpost -> {

                }
                R.id.ic_profile -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.ic_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            false
        }
    }

    private fun getNotifications(username: String) {
        val request = UpdatesRequest(username, null)
        Log.d("missed activity", request.toString())

        PostApi().getMissedActivity(request)
            .enqueue(object : Callback<Update> {
                override fun onFailure(call: Call<Update>, t: Throwable) {
                    Log.d("missed updates", t.message.toString())
                }

                override fun onResponse(
                    call: Call<Update>,
                    response: Response<Update>
                ) {
                    if (response.isSuccessful) {
                        Log.d("missed updates", "api call successful")
                        val updatesCollection = response.body()!!
                        Log.d("missed updates", updatesCollection.comments.size.toString())
                        adapter.setUpdates(updatesCollection.comments, username)
                    }
                }
            })
    }
}