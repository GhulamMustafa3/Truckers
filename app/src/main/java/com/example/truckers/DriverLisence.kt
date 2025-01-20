package com.example.truckers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DriverLisence : AppCompatActivity() {
    private lateinit var lisImg: ImageView
    private lateinit var addLBtn: Button

    private val REQUEST_IMAGE_CAPTURE_LICENSE = 1
    private val REQUEST_IMAGE_GALLERY_LICENSE = 2
    private var isLicenseImageTaken = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_driver_lisence)

        lisImg = findViewById(R.id.driving_license_image)
        addLBtn = findViewById(R.id.add_photo_license)

        // Show dialog to choose between Camera or Gallery
        addLBtn.setOnClickListener {
            showImageSourceDialog()
        }

        val nextBtn: Button = findViewById(R.id.next_button_license)
        nextBtn.setOnClickListener {
            if (isLicenseImageTaken) {
                // Save completion flag and proceed to the next activity
                val sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("driverLicenseCompleted", true).apply()

                val intent = Intent(this, vehicleinfo::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please take a photo of the driver's license first.", Toast.LENGTH_SHORT).show()
            }
        }
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
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_LICENSE)
        } else {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show()
        }
    }

    // Open the gallery to choose an image
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY_LICENSE)
    }

    // Handle the result of the camera or gallery intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_LICENSE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    lisImg.setImageBitmap(imageBitmap) // Set the captured image to ImageView
                    isLicenseImageTaken = true
                }
                REQUEST_IMAGE_GALLERY_LICENSE -> {
                    val selectedImageUri: Uri? = data?.data
                    lisImg.setImageURI(selectedImageUri) // Set the selected image from gallery to ImageView
                    isLicenseImageTaken = true
                }
            }
        }
    }
}
