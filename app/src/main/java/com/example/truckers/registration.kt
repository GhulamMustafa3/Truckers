package com.example.truckers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class registration : Fragment() {
    private lateinit var plateNo: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences =  requireContext().getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("VehicleInfo")

        plateNo = view.findViewById(R.id.registration_plate_input)
        val nextBtn: Button = view.findViewById(R.id.next_button)

        nextBtn.setOnClickListener {
            val plateNumber = plateNo.text.toString().trim()

            if (plateNumber.isNotEmpty()) {
                // Set the flag for registration info as completed


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
                    sharedPreferences.edit().putBoolean("registrationInfoCompleted", true).apply()
                    navigateToFragment(vehicleimage())
                }
                .addOnFailureListener {
                    // Failed to save to Firebase
                    showToast("Failed to save plate number")
                }
        }

    // Function to show a Toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // Adds the transaction to the back stack
        transaction.commit()
    }
}