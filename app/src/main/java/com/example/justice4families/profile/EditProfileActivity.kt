package com.example.justice4families.profile

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.justice4families.R

class EditProfileActivity : AppCompatActivity() {
    var didSaveEdits = true
    lateinit var profileName: EditText
    lateinit var profileEmail: EditText
    lateinit var profilePwd: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: Button = findViewById(R.id.edit_profile_back)
        val saveButton: TextView = findViewById(R.id.save_profile_btn)

        saveButton.setOnClickListener{
            // TODO: Network call to change the data
            didSaveEdits = true
        }

        backButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            if (didSaveEdits) {
                finish()
            } else {
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

        val data = intent
        var fullname: String = ""
        var email = data.getStringExtra("email")
        var password = data.getStringExtra("password")

        profileName = findViewById(R.id.edit_name_text_field)
        profileName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                didSaveEdits = (fullname == s.toString() && didSaveEdits)
            }

            override fun afterTextChanged(s: Editable?) {
                // TODO Auto-generated method stub
            }

        })

        profileEmail = findViewById(R.id.edit_email_text_field)
        profileEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                didSaveEdits = (email == s.toString() && didSaveEdits)
            }

            override fun afterTextChanged(s: Editable?) {
                // TODO Auto-generated method stub
            }
        })

        profilePwd = findViewById(R.id.edit_password_text_field)
        profilePwd.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                didSaveEdits = (password == s.toString() && didSaveEdits)
            }

            override fun afterTextChanged(s: Editable?) {
                // TODO Auto-generated method stub
            }
        })

        val profileImage : de.hdodenhof.circleimageview.CircleImageView = findViewById(R.id.edit_profile_pic)
        profileImage.setOnClickListener{
            Log.i("Edit Profile", "Clicked profile image circle")
        }

        profileName.setText(fullname)
        profileEmail.setText(email)
        profilePwd.setText(password)
    }
}