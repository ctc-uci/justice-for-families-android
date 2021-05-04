package com.example.justice4families

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        notificationRecyclerView.layoutManager = layoutManager


        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_addpost -> {

                }
                R.id.ic_profile -> {

                }
                R.id.ic_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }
}