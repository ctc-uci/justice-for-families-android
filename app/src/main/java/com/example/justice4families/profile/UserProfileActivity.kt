package com.example.justice4families.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.justice4families.MainActivity
import com.example.justice4families.LoginActivity
import com.example.justice4families.R
import com.example.justice4families.savedPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    var userName = savedPreferences.username
    lateinit var settingsMenu : Menu


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                supportFragmentManager
            )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        var loggedInUser = savedPreferences.loggedin
        val editProfileBtn: Button = findViewById(R.id.edit_profile_btn)
        val profileText: TextView = findViewById(R.id.profile_text)

        if(intent.hasExtra("post_username")){
            userName = intent.getStringExtra("post_username").toString()
            if(userName != savedPreferences.username) loggedInUser = false
        }

        user_name.text = userName

        if (!loggedInUser) {
            editProfileBtn.visibility = View.GONE
            profileText.visibility = View.GONE
            back_on_profile.visibility = View.VISIBLE
        } else {
            setSupportActionBar(profile_toolbar)
        }

        back_on_profile.setOnClickListener{
            onBackPressed()
        }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.profile_menu_items, menu)
        if (menu != null) {
            settingsMenu = menu
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}