package com.example.truckers

import android.app.Activity
import android.content.ContentValues
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
import androidx.appcompat.app.AlertDialog
import java.io.File
import java.io.FileOutputStream

class CNIC : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var cnicFrontImage: ImageView
    private lateinit var cnicBackImage: ImageView
    private var frontImagePath: String? = null
    private var backImagePath: String? = null

    private val REQUEST_IMAGE_CAPTURE_FRONT = 1
    private val REQUEST_IMAGE_CAPTURE_BACK = 2
    private val REQUEST_IMAGE_GALLERY_FRONT = 3
    private val REQUEST_IMAGE_GALLERY_BACK = 4
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cnic)

        // Initialize Database Helper
        databaseHelper = DatabaseHelper(this)
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
                saveToDatabase(cnicNumber, frontImagePath!!, backImagePath!!)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isCnicInfoComplete", true)
                editor.apply()
                showToast("Data saved successfully")

                // Proceed to the next activity
                val intent = Intent(this,DriverLisence::class.java) // Ensure VehicleInfo exists
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
            frontImagePath == null -> {
                showToast("Please add the front image of CNIC")
                false
            }
            backImagePath == null -> {
                showToast("Please add the back image of CNIC")
                false
            }
            else -> true
        }
    }

    // Function to save data to SQLite database
    private fun saveToDatabase(cnicNumber: String, frontImage: String, backImage: String) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CNIC_NUMBER, cnicNumber)
            put(DatabaseHelper.CNIC_FRONT_IMAGE_PATH, frontImage)
            put(DatabaseHelper.CNIC_BACK_IMAGE_PATH, backImage)
        }
        db.insert(DatabaseHelper.TABLE_CNIC, null, values)
        db.close()
    }

    // Function to save a bitmap to internal storage and return the file path
    private fun saveBitmapToInternalStorage(bitmap: Bitmap, filename: String): String {
        val file = File(filesDir, filename)
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return file.absolutePath
    }

    // Handle the result of the camera or gallery intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE_FRONT -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    frontImagePath = saveBitmapToInternalStorage(imageBitmap, "front_cnic.png")
                    cnicFrontImage.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_CAPTURE_BACK -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    backImagePath = saveBitmapToInternalStorage(imageBitmap, "back_cnic.png")
                    cnicBackImage.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_GALLERY_FRONT -> {
                    val selectedImageUri: Uri? = data?.data
                    frontImagePath = selectedImageUri?.path
                    cnicFrontImage.setImageURI(selectedImageUri)
                }
                REQUEST_IMAGE_GALLERY_BACK -> {
                    val selectedImageUri: Uri? = data?.data
                    backImagePath = selectedImageUri?.path
                    cnicBackImage.setImageURI(selectedImageUri)
                }
            }
        }
    }

    // Function to show a Toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Function to show the image source dialog
    private fun showImageSourceDialog(requestCodeCapture: Int, requestCodeGallery: Int) {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, requestCodeCapture)
                }
                1 -> {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, requestCodeGallery)
                }
            }
        }
        builder.show()
    }

    private fun loadSavedImages() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CNIC,
            arrayOf(DatabaseHelper.CNIC_FRONT_IMAGE_PATH, DatabaseHelper.CNIC_BACK_IMAGE_PATH),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val frontImageColumnIndex = cursor.getColumnIndex(DatabaseHelper.CNIC_FRONT_IMAGE_PATH)
            val backImageColumnIndex = cursor.getColumnIndex(DatabaseHelper.CNIC_BACK_IMAGE_PATH)

            // Check if the column index is valid (>= 0)
            if (frontImageColumnIndex >= 0) {
                val frontImagePath = cursor.getString(frontImageColumnIndex)
                if (frontImagePath.isNotEmpty()) {
                    cnicFrontImage.setImageURI(Uri.parse(frontImagePath)) // Load front image
                }
            }

            if (backImageColumnIndex >= 0) {
                val backImagePath = cursor.getString(backImageColumnIndex)
                if (backImagePath.isNotEmpty()) {
                    cnicBackImage.setImageURI(Uri.parse(backImagePath)) // Load back image
                }
            }
        }


        cursor?.close()
        db.close()
    }
}
