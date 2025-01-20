package com.example.truckers

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class businessregis : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profileImg: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private var isPhotoTaken = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_businessregis)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        profileImg = findViewById(R.id.Profileimage)
        val editIcon: ImageView = findViewById(R.id.editIcon)
        val companyname:TextInputEditText=findViewById(R.id.companyname_input)
        val username: TextInputEditText = findViewById(R.id.username_input)
        val email: TextInputEditText = findViewById(R.id.email_input)
        val password: TextInputEditText = findViewById(R.id.pass_input)
        val phone: TextInputEditText = findViewById(R.id.phone_input)
        val nextButton: Button = findViewById(R.id.next_button)

        // Set Click Listener for Edit Icon to show Image Source Dialog
        editIcon.setOnClickListener {
            showImageSourceDialog()
        }

        // Set Click Listener for Next Button
        nextButton.setOnClickListener {
            val usernameText = username.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val companyName=companyname.text.toString().trim()

            // Validate Inputs
            if (validateInputs(companyName,usernameText, emailText, passwordText, phoneText)) {
                // Save completion status in SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putBoolean("isBasicInfoComplete", true)
                editor.apply()

                // Proceed to the next activity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Function to validate all inputs
    private fun validateInputs(companyName:String,username: String, email: String, password: String, phone: String): Boolean {
        return when {
            companyName.isEmpty()->{
                showToast("Company Name is Required")
                false
            }
            username.isEmpty() -> {
                showToast("Username is required")
                false
            }
            email.isEmpty() -> {
                showToast("Email is required")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Enter a valid email")
                false
            }
            password.isEmpty() -> {
                showToast("Password is required")
                false
            }
            password.length < 6 -> {
                showToast("Password must be at least 6 characters long")
                false
            }
            phone.isEmpty() -> {
                showToast("Phone number is required")
                false
            }
            phone.length != 10 -> {
                showToast("Phone number must be 10 digits")
                false
            }
            else -> true
        }
    }

    // Function to show a Toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Show a dialog to let the user choose between Camera and Gallery
    private fun showImageSourceDialog() {
        val options = arrayOf("Take a Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera() // Camera option
                1 -> openGallery() // Gallery option
            }
        }
        builder.show()
    }

    // Open the camera to take a photo
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show()
        }
    }

    // Open the gallery to choose an image
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY)
    }

    // Handle the result of the camera or gallery intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    profileImg.setImageBitmap(imageBitmap) // Set image from camera
                    isPhotoTaken = true // Set flag to true when photo is taken
                }
                REQUEST_IMAGE_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    profileImg.setImageURI(selectedImageUri) // Set image from gallery
                    isPhotoTaken = true // Set flag to true when photo is selected from gallery
                }
            }
        }
    }
    }
