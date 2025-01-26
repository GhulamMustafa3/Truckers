package com.example.truckers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EquipLimits : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equip_limits)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Input fields
        val length: TextInputEditText = findViewById(R.id.length_input)
        val weight: TextInputEditText = findViewById(R.id.weight_input)
        val limits: TextInputEditText = findViewById(R.id.limits_input)
        val type: TextInputEditText = findViewById(R.id.Ttype_input)

        // ProgressBar
        progressBar = findViewById(R.id.progress_bar)

        // Button
        val btn: Button = findViewById(R.id.next_button)

        btn.setOnClickListener {
            // Validate inputs
            if (validateInputs(length, weight, limits, type)) {
                // Show progress bar
                progressBar.visibility = View.VISIBLE

                // Save data to Firebase
                val equipmentDetails = EquipmentDetails(
                    length = length.text.toString().trim(),
                    weight = weight.text.toString().trim(),
                    limits = limits.text.toString().trim(),
                    type = type.text.toString().trim()
                )

                saveEquipmentDetailsToFirebase(equipmentDetails)
            } else {
                Toast.makeText(this, "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Saves equipment details to Firebase Realtime Database.
     */
    private fun saveEquipmentDetailsToFirebase(details: EquipmentDetails) {
        val equipmentId = database.child("equipmentDetails").push().key // Generate a unique ID
        if (equipmentId != null) {
            database.child("equipmentDetails").child(equipmentId).setValue(details)
                .addOnSuccessListener {
                    // Hide progress bar
                    progressBar.visibility = View.GONE

                    Toast.makeText(this, "Equipment details saved successfully!", Toast.LENGTH_SHORT).show()

                    // Save completion status in SharedPreferences
                    val sharedPreferences = getSharedPreferences("VehicleInfoFlags", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("equipmentDetailsCompleted", true).apply()

                    // Proceed to the next activity
                    val intent = Intent(this, TrucRoutes::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    // Hide progress bar
                    progressBar.visibility = View.GONE

                    Toast.makeText(this, "Failed to save equipment details: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Hide progress bar
            progressBar.visibility = View.GONE

            Toast.makeText(this, "Error generating equipment ID. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Validates all input fields.
     * @return true if all fields are valid, false otherwise.
     */
    private fun validateInputs(
        length: TextInputEditText,
        weight: TextInputEditText,
        limits: TextInputEditText,
        type: TextInputEditText
    ): Boolean {
        var isValid = true

        // Validate Length
        val lengthText = length.text.toString().trim()
        if (lengthText.isEmpty() || !isPositiveNumber(lengthText)) {
            length.error = "Enter a valid positive length"
            isValid = false
        }

        // Validate Weight
        val weightText = weight.text.toString().trim()
        if (weightText.isEmpty() || !isPositiveNumber(weightText)) {
            weight.error = "Enter a valid positive weight"
            isValid = false
        }

        // Validate Limits
        val limitsText = limits.text.toString().trim()
        if (limitsText.isEmpty() || !(limitsText.equals("FULL", ignoreCase = true) || limitsText.equals("PARTIAL", ignoreCase = true))) {
            limits.error = "Enter valid limits (FULL or PARTIAL)"
            isValid = false
        }

        // Validate Type
        val typeText = type.text.toString().trim()
        if (typeText.isEmpty()) {
            type.error = "Enter a valid type"
            isValid = false
        }

        return isValid
    }

    /**
     * Checks if a string is a positive number.
     * @return true if the string represents a positive number, false otherwise.
     */
    private fun isPositiveNumber(value: String): Boolean {
        return try {
            val number = value.toDouble()
            number > 0
        } catch (e: NumberFormatException) {
            false
        }
    }
}

/**
 * Data class for equipment details.
 */
data class EquipmentDetails(
    val length: String,
    val weight: String,
    val limits: String,
    val type: String
)
