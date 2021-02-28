package com.example.justice4families.profile

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.justice4families.MainActivity
import com.example.justice4families.R
import com.example.justice4families.data.EditApi
import com.example.justice4families.model.EditProfileRequest
import com.example.justice4families.model.SignUpRequest
import com.example.justice4families.savedPreferences
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.Authenticator

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: Button = findViewById(R.id.edit_profile_back)

        val saveButton: Button = findViewById(R.id.saveProfileInfo)
        var saveClicked: Boolean = false

        var data = intent
        var email = data.getStringExtra("email")
        var password = data.getStringExtra("password")


        var profileEmail : EditText = findViewById(R.id.profileEmailAddress)
        var profilePass : EditText = findViewById(R.id.profilePassword)

        var profileImage : de.hdodenhof.circleimageview.CircleImageView = findViewById(R.id.edit_profile_pic)
        profileImage.setOnClickListener{

            Log.d("TAG", "Clicked")
        }

        profileEmail.setText(email)
        profilePass.setText(password)


        saveButton.setOnClickListener{
            saveClicked = true
            println(profileEmail.text.toString())
            println(profilePass.text.toString())
            updateProfileInfo(profileEmail.text.toString(), profilePass.text.toString(), "This is a test")
        }

        backButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            if(saveClicked){
                finish()
            }
            else{
                dialogBuilder.setMessage("Are you sure you want to discard your changes?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener {
                            dialogInterface, i -> finish()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialogInterface, i -> dialogInterface.cancel()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Discard Changes")
                alert.show()
            }
        }

    }

    private fun updateProfileInfo(username: String, password: String, profilePicture: String?) {
        EditApi().editUserPicture(EditProfileRequest(username, password, profilePicture))
            .enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println(t.message)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext,"Profile Updated", Toast.LENGTH_LONG).show()
                        println("This is the new username: " + username)
                        println("This is the new password: " + password)
                        println("This is the new profile pic id: " + profilePicture)
                        //savedPreferences.setUserName(username)
                    }
                    else {
                        println("response code not 200")
                        Toast.makeText(applicationContext, "Could not update profile", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

//    AuthenticationApi().registerUser(SignUpRequest(email, password1))
//    .enqueue(object: Callback<ResponseBody> {
//        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//            println(t.message)
//        }
//
//        override fun onResponse(
//            call: Call<ResponseBody>,
//            response: Response<ResponseBody>
//        ) {
//            if(response.code()==200) {
//                savedPreferences.setUserName(email)
//                val intent = Intent(applicationContext, MainActivity::class.java)
//                startActivity(intent)
//            }
//            else{
//                Toast.makeText(applicationContext, "Email or Password already exists", Toast.LENGTH_LONG).show()
//            }
//        }
//    })

}