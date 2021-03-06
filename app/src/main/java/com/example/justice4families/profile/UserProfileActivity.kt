package com.example.justice4families.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.justice4families.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {
    companion object {
        private const val EDIT_PROF = 1
    }

    private lateinit var bottomNav: BottomNavigationView
    var username = savedPreferences.username
    lateinit var settingsMenu: Menu
    lateinit var profileImage: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        var loggedInUser = savedPreferences.loggedin
        if(intent.hasExtra("post_username")){
            username = intent.getStringExtra("post_username").toString()
            if(username != savedPreferences.username) loggedInUser = false
        }

        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this, loggedInUser,
                supportFragmentManager
            )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val editProfileBtn: Button = findViewById(R.id.edit_profile_btn)
        val profileText: TextView = findViewById(R.id.profile_text)
        profileImage = findViewById(R.id.profile_pic)

        if(intent.hasExtra("post_username")){
            username = intent.getStringExtra("post_username").toString()
            if(username != savedPreferences.username) loggedInUser = false
        }

        val parsedUsername = parseUsername(username)

        full_name.text = parsedUsername
        user_name.text = "@$parsedUsername"

        getProfilePic(username, callback = {
            Log.d("Setting profile pic", it)
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.profileplaceholder)
                .error(R.drawable.profileplaceholder)
                .resize(110, 110)
                .onlyScaleDown()
                .centerCrop()
                .into(profileImage)
        })

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

        editProfileBtn.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            Log.d("Edit Profile Result", "starting edit for result")
            startActivityForResult(intent, EDIT_PROF)
        }

        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_addpost -> {

                }
                R.id.ic_profile -> {
                    if (!loggedInUser) {
                        val intent = Intent(this, UserProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.ic_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.ic_notification -> {
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                }
            }
            false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Edit Profile Result", "back from editing profile")
        if (requestCode == EDIT_PROF) {
            if (resultCode == Activity.RESULT_OK) {
                getProfilePic(username, callback = {
                    Log.d("Setting profile pic", it)
                    Picasso.get()
                        .load(it)
                        .placeholder(R.drawable.profileplaceholder)
                        .error(R.drawable.profileplaceholder)
                        .resize(110, 110)
                        .onlyScaleDown()
                        .centerCrop()
                        .into(profileImage)
                })
                Log.d("Edit Profile Result", "User changed profile picture")
            } else {
                Log.d("Edit Profile Result", "User did not change profile picture")
            }
        }
    }

}