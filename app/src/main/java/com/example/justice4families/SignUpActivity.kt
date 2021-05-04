package com.example.justice4families

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.example.justice4families.data.AuthenticationApi
import com.example.justice4families.model.SignUpRequest
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.hide_password
import kotlinx.android.synthetic.main.activity_signup.password
import kotlinx.android.synthetic.main.activity_signup.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    var isPasswordVisible:Boolean = false
    var isPasswordConfirmVisible:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        back_to_login.setOnClickListener {
            onBackPressed()
        }
        signup_button.setOnClickListener {
            pass_text.background = resources.getDrawable(R.drawable.rectangle_9, theme)
            confirm_text.background = resources.getDrawable(R.drawable.rectangle_9, theme)
            checkInfo(email_signup.text.toString(), password.pass_text.text.toString(), confirm_pass.confirm_text.text.toString())
        }
        hide_password.setOnClickListener{
            val cursorPosition: Int = pass_text.selectionStart
            if(!isPasswordVisible){
                pass_text.transformationMethod = HideReturnsTransformationMethod.getInstance()
                isPasswordVisible = true
                hide_password.isActivated = true
            } else{
                pass_text.transformationMethod = PasswordTransformationMethod.getInstance()
                isPasswordVisible = false
                hide_password.isActivated = false
            }
            pass_text.setSelection(cursorPosition);
        }
        hide_password_confirm.setOnClickListener{
            val cursorPosition: Int = confirm_text.selectionStart
            if(!isPasswordConfirmVisible){
                confirm_text.transformationMethod = HideReturnsTransformationMethod.getInstance()
                isPasswordConfirmVisible = true
                hide_password_confirm.isActivated = true
            } else{
                confirm_text.transformationMethod = PasswordTransformationMethod.getInstance()
                isPasswordConfirmVisible = false
                hide_password_confirm.isActivated = false
            }
            confirm_text.setSelection(cursorPosition);
        }
    }

    private fun checkInfo(email:String, password1:String, password2:String){
        if(email.isEmpty()){
            error_message_signup.text = "Please enter an email address!"
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error_message_signup.text = "Please enter a valid email address!"
        }
        else if(password1.isEmpty()) {
            error_message_signup.text = "Please enter a password!"
            pass_text.background = resources.getDrawable(R.drawable.error_rectangle, theme)

        }
        else if (password1 != password2 || password2.isEmpty()) {
            error_message_signup.text = "Your passwords don't match. Please try again!"
            confirm_text.background = resources.getDrawable(R.drawable.error_rectangle, theme)
        }
        else {
            if (validatePassword(password1)) {
                AuthenticationApi().registerUser(SignUpRequest(email, password1))
                    .enqueue(object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            println(t.message)
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if(response.code()==200) {
                                savedPreferences.username = email;
                                savedPreferences.loggedin = true;
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Toast.makeText(applicationContext, "Email or Password already exists", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            else {
                error_message_signup.text = "Your password doesn't meet the criteria below!"
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