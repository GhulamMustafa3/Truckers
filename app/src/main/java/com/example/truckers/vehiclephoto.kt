package com.example.truckers

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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

class vehiclephoto : AppCompatActivity() {
    private lateinit var truckImg: ImageView
    private lateinit var addPhoto: Button
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private var isPhotoTaken = false // Flag to track if photo is taken
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehiclephoto)

        // Initialize Database Helper
        databaseHelper = DatabaseHelper(this)

        truckImg = findViewById(R.id.truck_image)
        addPhoto = findViewById(R.id.add_photo)
        val nextBtn: Button = findViewById(R.id.next_button_vehicle_pic)

        // Open camera or gallery based on user's choice
        addPhoto.setOnClickListener {
            showImageSourceDialog()
        }

        // Next button logic
        nextBtn.setOnClickListener {
            if (isPhotoTaken) {
                // Save the image to internal storage and database
                val imagePath = saveImageToInternalStorage()

                // Save the photo path to the SQLite database
                savePhotoToDatabase(imagePath)

                // Proceed to the next activity and save the flag
                val sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("vehiclePhotoCompleted", true).apply()

                val intent = Intent(this, VehicleCertificate::class.java)
                startActivity(intent)
            } else {
                // Show a Toast if no photo is taken
                Toast.makeText(this, "Please take a photo of the vehicle first.", Toast.LENGTH_SHORT).show()
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
                    truckImg.setImageBitmap(imageBitmap) // Set image from camera
                    isPhotoTaken = true // Set flag to true when photo is taken
                }
                REQUEST_IMAGE_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    truckImg.setImageURI(selectedImageUri) // Set image from gallery
                    isPhotoTaken = true // Set flag to true when photo is selected from gallery
                }
            }
        }
    }

    // Save the image to internal storage and return the file path
    private fun saveImageToInternalStorage(): String {
        val bitmap = (truckImg.drawable as BitmapDrawable).bitmap
        val file = File(filesDir, "vehicle_photo.png")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return file.absolutePath
    }

    // Save the photo path to the SQLite database
    private fun savePhotoToDatabase(imagePath: String) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PHOTO_PATH, imagePath)
        }
        db.insert(DatabaseHelper.TABLE_VEHICLE_PHOTO, null, values)
        db.close()
    }
}
