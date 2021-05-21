package com.example.justice4families

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.justice4families.data.AuthenticationApi
import com.example.justice4families.data.ProfilePictureApi
import com.example.justice4families.model.EmailRequestBody
import com.example.justice4families.model.LikedPostRequest
import com.example.justice4families.model.LoginRequest
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.hide_password
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    var isPasswordVisible:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(savedPreferences.loggedin) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            email_text.background = resources.getDrawable(R.drawable.rectangle_9, theme)
            password_text.background = resources.getDrawable(R.drawable.rectangle_9, theme)
            error_message.text = ""
            var email : String = email_text.text.toString()
            var password : String = password.password_text.text.toString()
            if(validateLogin(email, password))  {
                loginRequest(email, password)
            }
        }

        hide_password.setOnClickListener {
            val cursorPosition: Int = password_text.selectionStart
            if(!isPasswordVisible){
                password_text.transformationMethod = HideReturnsTransformationMethod.getInstance()
                isPasswordVisible = true
                hide_password.isActivated = true
            } else{
                password_text.transformationMethod = PasswordTransformationMethod.getInstance()
                isPasswordVisible = false
                hide_password.isActivated = false
            }
            password_text.setSelection(cursorPosition);
        }

        join_now.setOnClickListener {
            //changed to view post activity for testing
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateLogin(email: String, password: String): Boolean {
        if(email.isEmpty()){
            error_message.text = "Please enter an email address!"
            return false
        }
        else if(password.isEmpty()) {
            error_message.text = "Please enter a password!"
            return false
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error_message.text = "Please Enter a valid email address!"
            return false
        }

        return true
    }

    private fun loginRequest(email: String, password: String) {
        AuthenticationApi().loginUser(LoginRequest(email, password))
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    println(t.message)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) {
                        savedPreferences.username = email
                        savedPreferences.loggedin = true

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else if(response.code() == 500) {
                        error_message.text = "Wrong username or password!"
                        email_text.background = resources.getDrawable(
                            R.drawable.error_rectangle,
                            theme
                        )
                        password_text.background = resources.getDrawable(
                            R.drawable.error_rectangle,
                            theme
                        )
                    }

                }
            })
    }
}