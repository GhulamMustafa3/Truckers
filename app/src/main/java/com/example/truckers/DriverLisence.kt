package com.example.truckers

import android.app.Activity
import android.content.ContentValues
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
import java.io.File
import java.io.FileOutputStream

class DriverLisence : AppCompatActivity() {
    private lateinit var lisImg: ImageView
    private lateinit var addLBtn: Button
    private lateinit var dbHelper: DatabaseHelper

    private val REQUEST_IMAGE_CAPTURE_LICENSE = 1
    private val REQUEST_IMAGE_GALLERY_LICENSE = 2
    private var isLicenseImageTaken = false
    private var licenseImagePath: String? = null // Store the image path or URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_driver_lisence)

        lisImg = findViewById(R.id.driving_license_image)
        addLBtn = findViewById(R.id.add_photo_license)

        dbHelper = DatabaseHelper(this)

        // Show dialog to choose between Camera or Gallery
        addLBtn.setOnClickListener {
            showImageSourceDialog()
        }

        val nextBtn: Button = findViewById(R.id.next_button_license)
        nextBtn.setOnClickListener {
            if (isLicenseImageTaken) {
                // Save the license image path to the database
                licenseImagePath?.let {
                    // Assuming driverId is available (you can modify this part as per your app logic)
                    val driverId = 1 // Example driver ID, update with actual logic
                    val result = dbHelper.insertDriverLicenseImage(driverId, it)
                    if (result != -1L) {
                        // Save completion flag and proceed to the next activity
                        val sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
                        sharedPreferences.edit().putBoolean("driverLicenseCompleted", true).apply()

                        val intent = Intent(this, EquipLimits::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to save driver's license image", Toast.LENGTH_SHORT).show()
                    }
                }
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
                    // Save the image path to internal storage
                    licenseImagePath = saveImageToStorage(imageBitmap)
                    isLicenseImageTaken = true
                }
                REQUEST_IMAGE_GALLERY_LICENSE -> {
                    val selectedImageUri: Uri? = data?.data
                    lisImg.setImageURI(selectedImageUri) // Set the selected image from gallery to ImageView
                    licenseImagePath = selectedImageUri.toString() // Save URI as string
                    isLicenseImageTaken = true
                }
            }
        }
    }

    // Save the captured image to internal storage and return the file path
    private fun saveImageToStorage(bitmap: Bitmap): String {
        // Create a unique filename
        val filename = "driver_license_${System.currentTimeMillis()}.png"
        // Create a file in the app's internal storage
        val file = File(filesDir, filename)
        // Save the image to the file
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        // Return the absolute path of the saved image
        return file.absolutePath
    }
}
