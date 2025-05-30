package com.example.truckers

import android.content.Intent

import android.os.Bundle

import android.view.View

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.example.truckers.databinding.ActivityShipperIndividualBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ShipperIndividual : AppCompatActivity() {

    private lateinit var binding: ActivityShipperIndividualBinding
    private lateinit var database: DatabaseReference // Firebase Database Reference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityShipperIndividualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Set Click Listener for Next Button
        binding.doneButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passInput.text.toString().trim()
            val phone = binding.phoneInput.text.toString().trim()

            // Validate Inputs
            if (validateInputs(username, email, password, phone)) {
                registerUser(username, email, password, phone)



            }
        }
    }

    // Function to validate all inputs
    private fun validateInputs(username: String, email: String, password: String, phone: String): Boolean {
        return when {
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

    // Function to register a user with Firebase Authentication
    private fun registerUser(username: String, email: String, password: String, phone: String) {
        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE

        // Register user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // Save user details to Realtime Database
                    saveToFirebase(username, email, phone)


                } else {
                    showToast("Registration failed: ${task.exception?.message}")
                }
            }
    }

    // Function to save user data to Firebase Realtime Database
    private fun saveToFirebase(username: String, email: String, phone: String) {
        val userId = auth.currentUser?.uid ?: return

        val user = mapOf(
            "username" to username,
            "email" to email,
            "phone" to phone
        )

        database.child("shippers").child(userId).setValue(user)
            .addOnSuccessListener {
                showToast("Data saved successfully")
                val intent = Intent(this, ShipperHome::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                showToast("Failed to save data: ${exception.message}")
            }
    }

    // Function to show a Toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    }

