package com.example.truckers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class equipmentlimits : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var progressBar: ProgressBar
    private lateinit var length: TextInputEditText
    private lateinit var weight: TextInputEditText
    private lateinit var limits: TextInputEditText
    private lateinit var type: TextInputEditText
    private lateinit var sharedPreferences: SharedPreferences
    private var savedLength: String? = null
    private var savedWeight: String? = null
    private var savedLimits: String? = null
    private var savedType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("users")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_equipmentlimits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        length = view.findViewById(R.id.length_input)
        weight = view.findViewById(R.id.weight_input)
        limits = view.findViewById(R.id.limits_input)
        type = view.findViewById(R.id.Ttype_input)
        progressBar = view.findViewById(R.id.progress_bar)
        sharedPreferences =  requireContext().getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)

        // Load existing data if available
        loadEquipmentDetails()

        // Button to save data
        val btn: Button = view.findViewById(R.id.next_button)
        btn.setOnClickListener {
            val Length = length.text.toString().trim()
            val Weight = weight.text.toString().trim()
            val Limits = limits.text.toString().trim()
            val Type = type.text.toString().trim()

            if (validateInputs(Length, Weight, Limits, Type)) {
                if (isDataModified(Length, Weight, Limits, Type)) {
                    saveEquipmentDetailsToFirebase(Length, Weight, Limits, Type)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Data unchanged; no need to save again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun loadEquipmentDetails() {




            database.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val data = snapshot.value as? Map<String, String>
                    data?.let {
                        savedLength = it["length"]
                        savedWeight = it["weight"]
                        savedLimits = it["limits"]
                        savedType = it["type"]

                        // Populate the fields with the saved data
                        length.setText(savedLength)
                        weight.setText(savedWeight)
                        limits.setText(savedLimits)
                        type.setText(savedType)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load equipment details.", Toast.LENGTH_SHORT).show()
            }

    }

    private fun isDataModified(length: String, weight: String, limits: String, type: String): Boolean {
        return length != savedLength || weight != savedWeight || limits != savedLimits || type != savedType
    }

    private fun saveEquipmentDetailsToFirebase(length: String, weight: String, limits: String, type: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid  // Get the UID of the logged-in user

            val trucksRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("trucks") // Reference the trucks node

            val truckId = trucksRef.push().key  // Generate a unique truck ID
            if (truckId == null) {
                Toast.makeText(requireContext(), "Error generating truck ID.", Toast.LENGTH_SHORT).show()
                return
            }

            val truckData = mapOf(
                "length" to length,
                "weight" to weight,
                "limits" to limits,
                "type" to type
            )

            progressBar.visibility = View.VISIBLE
            trucksRef.child(truckId).setValue(truckData)  // Save the truck details under the generated truck ID
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        val sharedPreferences = requireContext().getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
                        sharedPreferences.edit()
                            .putBoolean("equipmentDetailsComplete", true)
                            .apply()

                        navigateToFragment(truckroutes())
                    } else {
                        Toast.makeText(requireContext(), "Failed to save data. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "No user is logged in.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validateInputs(length: String, weight: String, limits: String, type: String): Boolean {
        var isValid = true

        if (length.isEmpty() || !isPositiveNumber(length)) {
            this.length.error = "Enter a valid positive length"
            isValid = false
        }

        if (weight.isEmpty() || !isPositiveNumber(weight)) {
            this.weight.error = "Enter a valid positive weight"
            isValid = false
        }

        if (limits.isEmpty() || !(limits.equals("FULL", ignoreCase = true) || limits.equals("PARTIAL", ignoreCase = true))) {
            this.limits.error = "Enter valid limits (FULL or PARTIAL)"
            isValid = false
        }

        if (type.isEmpty()) {
            this.type.error = "Enter a valid type"
            isValid = false
        }

        return isValid
    }

    private fun isPositiveNumber(value: String): Boolean {
        return try {
            val number = value.toDouble()
            number > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
