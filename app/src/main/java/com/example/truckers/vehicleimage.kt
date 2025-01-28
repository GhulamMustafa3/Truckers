package com.example.truckers

import android.app.Activity
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import java.io.File
import java.io.FileOutputStream

class vehicleimage : Fragment() {
    private lateinit var truckImg: ImageView
    private lateinit var addPhoto: Button
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private var isPhotoTaken = false // Flag to track if photo is taken
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicleimage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Database Helper
        databaseHelper = DatabaseHelper(requireContext())
        sharedPreferences =  requireContext().getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
        truckImg = view.findViewById(R.id.truck_image)
        addPhoto = view.findViewById(R.id.add_photo)
        val nextBtn: Button = view.findViewById(R.id.next_button_vehicle_pic)

        // Check if an image was previously saved and display it
        val savedImagePath = getSavedImagePath()
        if (savedImagePath != null) {
            val file = File(savedImagePath)
            if (file.exists()) {
                truckImg.setImageURI(Uri.fromFile(file)) // Set the saved image
                isPhotoTaken = true // Set flag to true if an image is loaded
            }
        }

        // Open camera or gallery based on user's choice
        addPhoto.setOnClickListener {
            showImageSourceDialog()
        }

        // Next button logic
        nextBtn.setOnClickListener {
            if (isPhotoTaken) {
                // Save the image to internal storage and database
                val imagePath = saveImageToInternalStorage()
                sharedPreferences.edit().putBoolean("vehiclePhotoCompleted", true).apply()
                // Save the photo path to the SQLite database
                savePhotoToDatabase(imagePath)
                navigateToFragment(vehicleregisterpic())
            } else {
                // Show a Toast if no photo is taken
                Toast.makeText(requireContext(), "Please take a photo of the vehicle first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Show a dialog to let the user choose between Camera and Gallery
    private fun showImageSourceDialog() {
        val options = arrayOf("Take a Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(requireContext())
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
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(requireContext(), "Camera is not available", Toast.LENGTH_SHORT).show()
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
        val file = File(requireContext().filesDir, "vehicle_photo.png")
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

    // Retrieve the saved image path from the database
    private fun getSavedImagePath(): String? {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_VEHICLE_PHOTO,
            arrayOf(DatabaseHelper.COLUMN_PHOTO_PATH),
            null,
            null,
            null,
            null,
            null
        )

        var imagePath: String? = null
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHOTO_PATH)
            if (columnIndex >= 0) {
                imagePath = cursor.getString(columnIndex)
            }
        }
        cursor?.close()
        db.close()
        return imagePath
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // Adds the transaction to the back stack
        transaction.commit()
    }
}
