package com.example.justice4families

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import com.example.justice4families.data.AuthenticationApi
import com.example.justice4families.model.LoginRequest
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

        login_button.setOnClickListener {
            var email : String = email.text.toString()
            var password : String = password.password_text.text.toString()
            if(validateLogin(email, password))  {
                loginRequest(email, password)
            }
        }

        hide_password.setOnClickListener {
            if(!isPasswordVisible){
                password_text.transformationMethod = HideReturnsTransformationMethod.getInstance()
                isPasswordVisible = true
            } else{
                password_text.transformationMethod = PasswordTransformationMethod.getInstance()
                isPasswordVisible = false
            }
        }

        join_now.setOnClickListener {
            //changed to view post activity for testing
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateLogin(email: String, password: String): Boolean {
        if(email.isEmpty()){
            Toast.makeText(this, "Please Enter an Email Address", Toast.LENGTH_LONG).show()
            return false
        }
        else if(password.isEmpty()) {
            Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_LONG).show()
            return false
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please Enter a valid Email Address", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun loginRequest(email: String, password: String) {
        AuthenticationApi().loginUser(LoginRequest(email, password))
            .enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG).show()
                    println(t.message)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful)
                    {
                        savedPreferences.setUserName(email)
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else if(response.code() == 500) {
                        Toast.makeText(applicationContext, "Error logging in", Toast.LENGTH_LONG).show()
                    }

                }
            })
    }


}