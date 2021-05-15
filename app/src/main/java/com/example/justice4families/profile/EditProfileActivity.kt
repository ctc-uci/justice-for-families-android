package com.example.justice4families.profile

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.justice4families.getFileName
import com.example.justice4families.data.ProfilePictureApi
import com.example.justice4families.model.S3ContentType
import com.example.justice4families.model.S3URL
import com.example.justice4families.model.UploadRequestBody
import com.example.justice4families.R
import com.example.justice4families.savedPreferences
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {
    companion object {
        private const val IMAGE_PICK_CODE = 1
    }

    var didSaveEdits = true
    lateinit var profileName: EditText
    lateinit var profileEmail: EditText
    lateinit var profilePwd: EditText
    lateinit var profileImageView : de.hdodenhof.circleimageview.CircleImageView
    private var selectedImgUri: Uri? = null
    private var selectedImgMimeType: String? = null
    var fullname: String = ""
    var email: String = ""
    var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton: Button = findViewById(R.id.edit_profile_back)
        val saveButton: TextView = findViewById(R.id.save_profile_btn)

        saveButton.setOnClickListener {
            Log.i("Edit Profile", "Save button clicked")
            if (!didSaveEdits) {
                if (selectedImgUri != null && selectedImgMimeType != null) {
                    uploadImage(selectedImgUri!!, selectedImgMimeType!!)
                }

                // TODO: Fullname, email, password
                fullname = profileName.text.toString()
                email = profileEmail.text.toString()
                password = profilePwd.text.toString()
            }

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

        profileImageView = findViewById(R.id.edit_profile_pic)
        profileImageView.setOnClickListener {
            pickImageFromGallery()
        }

        val editPicBtn: TextView = findViewById(R.id.edit_profile_pic_btn)
        editPicBtn.setOnClickListener {
            pickImageFromGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    selectedImgUri = data.data
                    selectedImgMimeType = data.data?.let { contentResolver.getType(it) }
                    profileImageView.setImageURI(selectedImgUri)
                    didSaveEdits = false
                } else {
                    Log.i("Edit Profile", "Data returned is null")
                }
            } else {
                Log.i("Edit Profile", "User did not select image")
            }
        }
    }

    private fun pickImageFromGallery() {
        Log.i("Edit Profile", "User opened image gallery")
        val galleryIntent = Intent(
            Intent.ACTION_GET_CONTENT,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        galleryIntent.type = "image/*"
        val mimeTypes = S3ContentType.getAllValues()
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(galleryIntent, IMAGE_PICK_CODE)
    }

    private fun uploadImage(uri: Uri, type: String) {
        Log.i("Edit Profile", "Attempting to upload image")

        val contentType: S3ContentType? = S3ContentType.getFromValue(type)
        if (contentType != null ) {
            ProfilePictureApi().getS3SignedURL(contentType)
                .enqueue(object : Callback<S3URL> {
                    override fun onFailure(call: Call<S3URL>, t: Throwable) {
                        Log.e("Edit Profile - S3 URL", t.localizedMessage.toString())
                    }

                    override fun onResponse(
                        call: Call<S3URL>,
                        response: Response<S3URL>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            Log.i("Edit Profile - S3 URL", response.body().toString())
                            val uploadURL = response.body()!!.uploadURL!!
                            val path = response.body()!!.path!!
                            val body = createImageRequestBody(uri, contentType.mimeType)
                            if (body != null) {
                                storeImage(uploadURL, path, body)
                            } else {
                                Log.e("Edit Profile - S3 URL", "Problem creating request body")
                                displayError()
                            }
                        } else {
                            Log.w("Edit Profile - S3 URL", response.errorBody().toString())
                        }
                    }
                })
        } else {
            Log.w("Edit Profile - S3 URL", "Invalid contentType")
            displayError()
        }
    }

    private fun createImageRequestBody(uri: Uri, type: String) : RequestBody? {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: return null

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(uri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        return UploadRequestBody(file, type)
    }

    private fun storeImage(uploadURL: String, path: String, body: RequestBody) {
        ProfilePictureApi().putImageToS3SignedURL(uploadURL, body)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Edit Profile - PUT", "Failure: ${t.localizedMessage}")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.i("Edit Profile - PUT", "Successfully stored image in AWS S3")
                        updateImage(path)
                    } else {
                        Log.w("Edit Profile - PUT", response.errorBody()?.string() ?: "Error")
                        displayError()
                        didSaveEdits = false
                    }
                }
            })
    }

    private fun updateImage(path: String) {
        val jsonObject = JSONObject()
        jsonObject.put("email", savedPreferences.username)
        jsonObject.put("picture", path)

        val jsonObjectString = jsonObject.toString()

        val request = RequestBody.create(
            MediaType.parse("application/json"),
            jsonObjectString
        )

        ProfilePictureApi().updateProfilePicture(request)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Edit Profile - POST", t.localizedMessage.toString())
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.i("Edit Profile - POST", "Successfully updated image")
                    } else {
                        Log.w("Edit Profile - POST",
                            "Error updating: ${response.errorBody()?.string()}")
                        displayError()
                        didSaveEdits = false
                    }
                }
            })
    }

    private fun displayError() {
        Toast.makeText(
            applicationContext,
            "Error saving profile picture.",
            Toast.LENGTH_LONG
        ).show()
    }
}