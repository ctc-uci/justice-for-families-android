package com.example.justice4families.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.justice4families.LoginActivity
import com.example.justice4families.R
import com.example.justice4families.savedPreferences
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        edit_profile_section.setOnClickListener {
            // TODO: Link to edit profile screen
        }

        tags_button.setOnClickListener {

        }

        logout_button.setOnClickListener {
            logoutUser()
        }

        delete_acct_button.setOnClickListener {
            Log.i("User Settings", "User tapped delete account button")
        }
    }

    private fun logoutUser() {
        savedPreferences.loggedin = false
        savedPreferences.username = "Anonymous"
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // prevent back button from returning user to settings
    }
}