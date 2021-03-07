package com.example.justice4families.profile

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.example.justice4families.MainActivity
import com.example.justice4families.R
import com.example.justice4families.data.EditApi
import com.example.justice4families.model.EditProfileRequest
import com.example.justice4families.model.SignUpRequest
import com.example.justice4families.savedPreferences
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_edit_profile.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.Authenticator
import java.net.URI

class EditProfileActivity : AppCompatActivity() {
    val PHOTO_REQUEST_CODE = 100
    lateinit var profileImage : de.hdodenhof.circleimageview.CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: Button = findViewById(R.id.edit_profile_back)

        val changeProfilePicButton: Button = findViewById(R.id.change_profile_pic_button)
        profileImage = findViewById(R.id.edit_profile_pic)

        val saveButton: Button = findViewById(R.id.saveProfileInfo)
        var saveClicked: Boolean = false


        var data = intent
        var email = data.getStringExtra("email")
        var password = data.getStringExtra("password")


        var profileEmail : EditText = findViewById(R.id.profileEmailAddress)
        var profilePass : EditText = findViewById(R.id.profilePassword)

        profileImage.setOnClickListener{
            Log.d("TAG", "Clicked")
        }

        changeProfilePicButton.setOnClickListener {
            val photoIntent = Intent(Intent.ACTION_PICK)
            photoIntent.type = "image/*"
            startActivityForResult(photoIntent, PHOTO_REQUEST_CODE)
        }

        profileEmail.setText(email)
        profilePass.setText(password)


        saveButton.setOnClickListener{
            saveClicked = true
            // println(profileEmail.text.toString())
            // println(profilePass.text.toString())
            updateProfileInfo(profileEmail.text.toString(), password.toString(), profilePass.text.toString(), "https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            profileImage.setImageURI(data?.data)
        }
    }

    private fun updateProfileInfo(username: String, password: String, newPassword: String, profilePicture: String) {
        val pictureURI = profilePicture.toUri()

        EditApi().editUserPicture(EditProfileRequest(username, password, newPassword, pictureURI))
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println(t.message)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, "Picture Updated", Toast.LENGTH_LONG).show()
                        println("This is the response code: " + response.code())
                        println("This is the profile picture URI: " + pictureURI.toString())
                    }
                    else {
                        Toast.makeText(applicationContext, "Picture update failed", Toast.LENGTH_LONG).show()
                        println("This is the response code: " + response.code())
                        println("This is the profile picture URI: " + pictureURI.toString())
                    }
                }
            })

        if (username.isEmpty()) {
            Toast.makeText(this, "Please Enter a Username", Toast.LENGTH_LONG).show()
        }

        else if(newPassword.isEmpty()) {
            Toast.makeText(this, "Please Enter a Password", Toast.LENGTH_LONG).show()
        }

            /*
        else {
            if (validatePassword(newPassword)) {
                EditApi().editUserPassword(EditProfileRequest(username, password, newPassword, pictureURI))
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            println(t.message)
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.code() == 200) {
                                Toast.makeText(
                                    applicationContext,
                                    "Profile Updated",
                                    Toast.LENGTH_LONG
                                ).show()
                                println("This is the new username: " + username)
                                println("This is the new password: " + password)
                                println("This is the new profile pic uri: " + pictureURI.toString())
                            } else {
                                println("This is the new username: " + username)
                                println("This is the old password: " + password)
                                println("This is the new password: " + newPassword)
                                println("This is the new profile pic uri: " + pictureURI.toString())
                                println("the response code is: " + response.code())
                                Toast.makeText(
                                    applicationContext,
                                    "Could not update profile",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    })
            } else {
                Toast.makeText(this, "Password does not meet requirements", Toast.LENGTH_LONG)
                    .show()
                }
        }
        */
    }

    private fun validatePassword(password:String): Boolean {
        /*
         Password requirements:
         Minimum length: 8
         Require: numbers, special character(!@#$%^&*), uppercase letter, lowercase letter
         */
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        val pattern = PASSWORD_PATTERN.toRegex()

        return pattern.matches(password)
    }

}
