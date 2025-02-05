package com.example.truckers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Profile() : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: TextView
    private lateinit var phone: TextView
    private lateinit var signout: TextView
    private lateinit var switch: TextView
    private lateinit var profileImage: ImageView
    private lateinit var editIcon: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileImage = view.findViewById(R.id.imageView)
        editIcon = view.findViewById(R.id.editIcon)
        // Initialize FirebaseAuth and DatabaseReference
        editIcon.setOnClickListener {
            openGallery()
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize UI components
        email = view.findViewById(R.id.email)
        phone = view.findViewById(R.id.phone)
        signout = view.findViewById(R.id.signout)
        switch = view.findViewById(R.id.Shipper_Broker)

        // Fetch user data from Firebase
        fetchUserData()

        // Set up the Sign Out button click listener
        signout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish() // Close the current activity to prevent back navigation
        }

        // Set up the Switch Profile button click listener
        switch.setOnClickListener {
            Toast.makeText(requireContext(), "Switching Profile...", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    // Function to fetch user data from Firebase
    private fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            if (userId.isNotEmpty()) {
                val userRef = database.child("users").child(userId)

                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) = if (snapshot.exists()) {
                        val userEmail = snapshot.child("email").getValue(String::class.java) ?: "No email found"
                        val userPhone = snapshot.child("phone").getValue(String::class.java) ?: "No phone found"

                        // Assign values to UI elements
                        email.text = userEmail
                        phone.text = userPhone
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Failed to load user data: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No user is logged in", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            profileImage.setImageURI(imageUri)
        }
    }

}
