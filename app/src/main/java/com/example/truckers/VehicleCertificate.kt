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

class VehicleCertificate : AppCompatActivity() {
    private lateinit var fImg: ImageView
    private lateinit var bImg: ImageView
    private lateinit var addFImg: Button
    private lateinit var addBImg: Button

    private val REQUEST_IMAGE_CAPTURE_FRONT = 1
    private val REQUEST_IMAGE_CAPTURE_BACK = 2
    private val REQUEST_IMAGE_GALLERY_FRONT = 3
    private val REQUEST_IMAGE_GALLERY_BACK = 4

    private var isFrontImageTaken = false
    private var isBackImageTaken = false
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_certificate)

        // Initialize Database Helper
        databaseHelper = DatabaseHelper(this)

        fImg = findViewById(R.id.regis_front_image)
        bImg = findViewById(R.id.regis_back_image)
        addFImg = findViewById(R.id.add_photo_front)
        addBImg = findViewById(R.id.add_photo_back)

        addFImg.setOnClickListener {
            showImageSourceDialog(REQUEST_IMAGE_CAPTURE_FRONT, REQUEST_IMAGE_GALLERY_FRONT)
        }

        addBImg.setOnClickListener {
            showImageSourceDialog(REQUEST_IMAGE_CAPTURE_BACK, REQUEST_IMAGE_GALLERY_BACK)
        }

        val nextBtn: Button = findViewById(R.id.next_button)
        nextBtn.setOnClickListener {
            if (isFrontImageTaken && isBackImageTaken) {
                // Save images to internal storage and database
                val frontImagePath = saveImageToInternalStorage(fImg)
                val backImagePath = saveImageToInternalStorage(bImg)

                // Save the image paths to the SQLite database
                saveImagePathsToDatabase(frontImagePath, backImagePath)

                // Save completion flag and proceed to the next activity
                val sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("certificateInfoCompleted", true).apply()

                val intent = Intent(this, DriverLisence::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please provide both front and back images of the certificate.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImageSourceDialog(frontRequestCode: Int, backRequestCode: Int) {
        val options = arrayOf("Take a Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera(frontRequestCode) // Camera option
                1 -> openGallery(backRequestCode) // Gallery option
            }
        }
        builder.show()
    }

    private fun openCamera(requestCode: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, requestCode)
        } else {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery(requestCode: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_FRONT -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    fImg.setImageBitmap(imageBitmap) // Set front image
                    isFrontImageTaken = true
                }
                REQUEST_IMAGE_CAPTURE_BACK -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    bImg.setImageBitmap(imageBitmap) // Set back image
                    isBackImageTaken = true
                }
                REQUEST_IMAGE_GALLERY_FRONT -> {
                    val selectedImageUri: Uri? = data?.data
                    fImg.setImageURI(selectedImageUri) // Set front image from gallery
                    isFrontImageTaken = true
                }
                REQUEST_IMAGE_GALLERY_BACK -> {
                    val selectedImageUri: Uri? = data?.data
                    bImg.setImageURI(selectedImageUri) // Set back image from gallery
                    isBackImageTaken = true
                }
            }
        }
    }

    // Save the image to internal storage and return the file path
    private fun saveImageToInternalStorage(imageView: ImageView): String {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val file = File(filesDir, "vehicle_certificate_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return file.absolutePath
    }

    // Save the front and back image paths to the SQLite database
    private fun saveImagePathsToDatabase(frontImagePath: String, backImagePath: String) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.VREGISTER_FRONT_IMAGE_PATH, frontImagePath)
            put(DatabaseHelper.VREGISTER_BACK_IMAGE_PATH, backImagePath)
        }
        db.insert(DatabaseHelper.TABLE_VEHICLE_CERTIFICATE, null, values)
        db.close()
    }
}
