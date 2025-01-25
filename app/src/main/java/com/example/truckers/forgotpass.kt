package com.example.truckers

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.truckers.databinding.ActivityForgotpassBinding
import com.google.firebase.auth.FirebaseAuth

class forgotpass : AppCompatActivity() {
    lateinit var binding: ActivityForgotpassBinding
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotpassBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()


        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()

            if (validateInput(email)) {
                sendPasswordResetEmail(email)
            }
        }
    }

    private fun validateInput(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                showToast("Email cannot be empty")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Enter a valid email address")
                false
            }
            else -> true
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Password reset email sent. Please check your inbox.")
                    finish() // Close the activity
                } else {
                    showToast("Failed to send reset email: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}