package com.example.truckers

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CNIC : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cnicFrontImage: ImageView
    private lateinit var cnicBackImage: ImageView
    private var isFrontImageSelected = false
    private var isBackImageSelected = false

    private val REQUEST_IMAGE_CAPTURE_FRONT = 1
    private val REQUEST_IMAGE_CAPTURE_BACK = 2
    private val REQUEST_IMAGE_GALLERY_FRONT = 3
    private val REQUEST_IMAGE_GALLERY_BACK = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cnic)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("DriverRegistrationPrefs", MODE_PRIVATE)

        // Initialize Views
        cnicFrontImage = findViewById(R.id.cnic_front_image)
        cnicBackImage = findViewById(R.id.cnic_back_image)
        val btnFrontAdd: Button = findViewById(R.id.add_photo_front)
        val btnBackAdd: Button = findViewById(R.id.add_photo_back)
        val cnicNumberInput: EditText = findViewById(R.id.cnic_number_input)
        val nextButton: Button = findViewById(R.id.next_button_cnic)

        // Handle Front Image Selection
        btnFrontAdd.setOnClickListener {
            showImageSourceDialog(REQUEST_IMAGE_CAPTURE_FRONT, REQUEST_IMAGE_GALLERY_FRONT)
        }

        // Handle Back Image Selection
        btnBackAdd.setOnClickListener {
            showImageSourceDialog(REQUEST_IMAGE_CAPTURE_BACK, REQUEST_IMAGE_GALLERY_BACK)
        }

        // Handle Next Button Click
        nextButton.setOnClickListener {
            val cnicNumber = cnicNumberInput.text.toString().trim()

            if (validateInputs(cnicNumber)) {
                // Save completion status in SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putBoolean("isCnicInfoComplete", true)
                editor.apply()

                // Proceed to the next activity
                val intent = Intent(this, vehicleinfo::class.java)
                startActivity(intent)
            }
        }
    }

    // Function to validate inputs
    private fun validateInputs(cnicNumber: String): Boolean {
        return when {
            cnicNumber.isEmpty() -> {
                showToast("CNIC number is required")
                false
            }
            cnicNumber.length != 13 -> {
                showToast("CNIC number must be 13 digits")
                false
            }
            !isFrontImageSelected -> {
                showToast("Please add the front image of CNIC")
                false
            }
            !isBackImageSelected -> {
                showToast("Please add the back image of CNIC")
                false
            }
            else -> true
        }
    }

    // Function to show a dialog to choose between Camera and Gallery
    private fun showImageSourceDialog(cameraRequestCode: Int, galleryRequestCode: Int) {
        val options = arrayOf("Take a Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera(cameraRequestCode) // Camera option
                1 -> openGallery(galleryRequestCode) // Gallery option
            }
        }
        builder.show()
    }

    // Open the camera to take a photo
    private fun openCamera(requestCode: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, requestCode)
        } else {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show()
        }
    }

    // Open the gallery to choose an image
    private fun openGallery(requestCode: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, requestCode)
    }

    // Handle the result of the camera or gallery intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_FRONT -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    cnicFrontImage.setImageBitmap(imageBitmap) // Set front image
                    isFrontImageSelected = true
                }
                REQUEST_IMAGE_CAPTURE_BACK -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    cnicBackImage.setImageBitmap(imageBitmap) // Set back image
                    isBackImageSelected = true
                }
                REQUEST_IMAGE_GALLERY_FRONT -> {
                    val selectedImageUri: Uri? = data?.data
                    cnicFrontImage.setImageURI(selectedImageUri) // Set front image from gallery
                    isFrontImageSelected = true
                }
                REQUEST_IMAGE_GALLERY_BACK -> {
                    val selectedImageUri: Uri? = data?.data
                    cnicBackImage.setImageURI(selectedImageUri) // Set back image from gallery
                    isBackImageSelected = true
                }
            }
        }
    }

    // Function to show a Toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
