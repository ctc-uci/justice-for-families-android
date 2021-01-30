package com.example.justice4families

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.justice4families.data.AuthenticationApi
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        back_to_login.setOnClickListener {
            onBackPressed()
        }
        signup_button.setOnClickListener {
            checkInfo(email_signup.email_signup_text.text.toString(), password.pass_text.text.toString(), confirm_pass.confirm_text.text.toString())
        }
    }

    private fun checkInfo(email:String, password1:String, password2:String){
        if(email.isEmpty()){
            Toast.makeText(this, "Please Enter an Email Address", Toast.LENGTH_LONG).show()
        }
        else if(password1.isEmpty() or password2.isEmpty()) {
            Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_LONG).show()
        }
        else if (password1 != password2) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_LONG).show()
        }
        else {
            if (validatePassword(password1)) {
                AuthenticationApi().registerUser(email, password1)
                    .enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            println(t.message)
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                    })
            }
            else {
                Toast.makeText(this, "Password does not meet requirements", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validatePassword(password:String): Boolean {
        /*
         Password requirements:
         Minimum length: 8
         Require: numbers, special character(!@#$%^&*), uppercase letter, lowercase letter
         */
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        var pattern = PASSWORD_PATTERN.toRegex()

        return pattern.matches(password)
    }
}