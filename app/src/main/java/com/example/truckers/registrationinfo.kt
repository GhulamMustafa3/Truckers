package com.example.truckers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class registrationinfo : AppCompatActivity() {
    private lateinit var plateNo: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrationinfo)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("VehicleInfo")

        plateNo = findViewById(R.id.registration_plate_input)
        val nextBtn: Button = findViewById(R.id.next_button)

        nextBtn.setOnClickListener {
            val plateNumber = plateNo.text.toString().trim()

            if (plateNumber.isNotEmpty()) {
                // Set the flag for registration info as completed
                sharedPreferences.edit().putBoolean("registrationInfoCompleted", true).apply()

                // Save the plate number to Firebase
                savePlateNumberToFirebase(plateNumber)

                // Navigate to the next activity

            } else {
                plateNo.error = "Please enter the plate number"
            }
        }
    }

    // Function to save plate number to Firebase
    private fun savePlateNumberToFirebase(plateNumber: String) {
        // Create a new entry in the Firebase database
        val vehicleId = database.push().key ?: return
        val vehicleData = mapOf("plateNumber" to plateNumber)

        // Save the data to Firebase under the unique vehicleId
        database.child(vehicleId).setValue(vehicleData)
            .addOnSuccessListener {
                // Successfully saved to Firebase
                showToast("Plate number saved successfully")
                val intent = Intent(this, vehiclephoto::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                // Failed to save to Firebase
                showToast("Failed to save plate number")
            }
    }

    // Function to show a Toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
