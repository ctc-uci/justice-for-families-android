package com.example.justice4families

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.view.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        back_to_login.setOnClickListener {
            onBackPressed()
        }
        signup_button.setOnClickListener {
            checkPassword(password.pass_text.text.toString(), confirm_pass.confirm_text.text.toString())
        }
    }

    private fun checkPassword(password1:String, password2:String){
        if(password1.isEmpty() or password2.isEmpty())
             Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_LONG).show()
        else if(password1!=password2){
             Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()
        }
        else{
            //make sure characteristics of password is correct
        }
    }
}