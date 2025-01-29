package com.example.truckers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.truckers.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()



        // Handle Login button click
        binding.login.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passInput.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailInput.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passInput.error = "Password is required"
                return@setOnClickListener
            }

            loginUser(email, password)
        }
        binding.forgot.setOnClickListener{
            val intent = Intent(this, forgotpass::class.java)
            startActivity(intent)
        }
        binding.signup.setOnClickListener{
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    val databaseRef = FirebaseDatabase.getInstance().reference

                    // Check if user exists in 'users' (Drivers)
                    databaseRef.child("users").child(userId!!).get()
                        .addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                navigateToHome("driver") // Redirect to Driver Home
                            } else {
                                // Check if user exists in 'shippers'
                                databaseRef.child("shippers").child(userId).get()
                                    .addOnSuccessListener { shipperSnapshot ->
                                        if (shipperSnapshot.exists()) {
                                            navigateToHome("shipper") // Redirect to Shipper Home
                                        } else {
                                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error fetching shipper data", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun navigateToHome(userType: String?) {
        val intent = when (userType) {
            "driver" -> Intent(this, HomeActivity::class.java)
            "shipper" -> Intent(this, ShipperHome::class.java)
            else -> Intent(this, HomeActivity::class.java) // Default fallback
        }
        startActivity(intent)
        finish()
    }



}
