package com.example.justice4families.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.justice4families.R
import com.example.justice4families.savedPreferences
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

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
        user_name.text = savedPreferences.getUserName()
        var loggedInUser = true
        val editProfileBtn :Button = findViewById(R.id.edit_profile_btn)

        if(intent.hasExtra("post_username")){
            user_name.text = intent.getStringExtra("post_username").toString()
            loggedInUser = false
        }

        if(!loggedInUser){
            editProfileBtn.visibility = View.GONE
        }

    }
}