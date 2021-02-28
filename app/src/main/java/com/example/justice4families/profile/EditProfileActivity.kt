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
import com.example.justice4families.R
import com.example.justice4families.model.SignUpRequest
import com.example.justice4families.savedPreferences
import de.hdodenhof.circleimageview.CircleImageView
import java.net.Authenticator

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: Button = findViewById(R.id.edit_profile_back)

        val saveButton: Button = findViewById(R.id.saveProfileInfo)
        var saveClicked: Boolean = false

        saveButton.setOnClickListener{
            saveClicked = true
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

        var data = intent
        var email = data.getStringExtra("email")
        var password = data.getStringExtra("password")


        var profileEmail : EditText = findViewById(R.id.profileEmailAddress)
        var profilePass : EditText = findViewById(R.id.profilePassword)

        var profileImage : de.hdodenhof.circleimageview.CircleImageView = findViewById(R.id.edit_profile_pic)
        profileImage.setOnClickListener{
            Log.d("TAG", "CLicked")
        }

        profileEmail.setText(email)
        profilePass.setText(password)
    }
}