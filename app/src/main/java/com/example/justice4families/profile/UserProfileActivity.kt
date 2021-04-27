package com.example.justice4families.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.justice4families.LoginActivity
import com.example.justice4families.R
import com.example.justice4families.savedPreferences
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.post_item.*

class UserProfileActivity : AppCompatActivity() {
    var userName = savedPreferences.username
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

        if(!loggedInUser){
            editProfileBtn.visibility = View.GONE
            profileText.visibility = View.GONE
            back_on_profile.visibility = View.VISIBLE
        }
        else{
            profile_toolbar.inflateMenu(R.menu.menu_items)
        }

        profile_toolbar.setOnMenuItemClickListener { item->
            if(item.itemId == R.id.logout) {
                savedPreferences.loggedin = false
                savedPreferences.username = "Anonymous"
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            true
        }



        back_on_profile.setOnClickListener{
            onBackPressed()
        }
    }

}