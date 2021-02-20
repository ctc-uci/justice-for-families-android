package com.example.justice4families.profile

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.justice4families.R

class EditProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: Button = findViewById(R.id.edit_profile_back)

        backButton.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)

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