package com.example.truckers

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.truckers.databinding.FragmentProfileBinding
import com.example.truckers.databinding.FragmentShipperProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ShipperProfile : Fragment() {

private var _binding:FragmentShipperProfileBinding?=null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShipperProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Fetch user data from Firebase
        fetchUserData()

        // Set up the Sign Out button click listener
        binding.signout.setOnClickListener {
            // Sign out the user and redirect to the login activity
            auth.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish() // Close the current activity to prevent back navigation
        }

        // Example of setting up a click listener for the Switch Profile button
        binding.ShipperBroker.setOnClickListener {
            // Handle switching profile action
            Toast.makeText(requireContext(), "Switching Profile...", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return root
    }

    // Function to fetch user data from Firebase
    private fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid // Get the UID of the current user

            if (userId.isNotEmpty()) {
                // Reference to the user's data in Firebase Realtime Database
                val userRef = database.child("shippers").child(userId)

                // Fetch the user data
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Extract user data

                            val email = snapshot.child("email").value.toString()
                            val phone = snapshot.child("phone").value.toString()

                            // Set the retrieved data into the UI elements
                            binding.email.text = email
                            binding.phone.text = phone

                        } else {
                            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding to avoid memory leaks
        _binding = null
    }

}


