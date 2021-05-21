package com.example.justice4families

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ProxyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        savedPreferences.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proxy)

        if(savedPreferences.loggedin)
        {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}