package com.example.truckers
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.truckers.databinding.FragmentLoaddetailsformBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

class loaddetailsform : Fragment() {
    private val calendar = Calendar.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("shippers")
    private lateinit var binding: FragmentLoaddetailsformBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoaddetailsformBinding.inflate(inflater, container, false)

        // Set up listeners after initializing binding
        binding.pickupdateinput.setOnClickListener { showDatePicker() }
        binding.dropoffdateinput.setOnClickListener{showDatePicker2()}
        binding.pickuptimeInput.setOnClickListener { showTimePicker(binding.pickuptimeInput) }
        binding.droptimeInput.setOnClickListener { showTimePicker(binding.droptimeInput) }
        binding.nextButton.setOnClickListener { validateAndSubmit() }

        return binding.root
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.pickupdateinput.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
    private fun showDatePicker2() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.dropoffdateinput.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker(targetEditText: com.google.android.material.textfield.TextInputEditText) {
        try {
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    // Convert 24-hour time to 12-hour format and determine AM/PM
                    val amPm = if (hourOfDay < 12) "AM" else "PM"
                    val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12  // Convert 00:xx to 12:xx for 12-hour format

                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm)
                    targetEditText.setText(formattedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // Set to false to enable AM/PM selection
            )
            timePicker.show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error opening time picker", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validateAndSubmit() {
        val origin = binding.orignInput.text.toString().trim()
        val destination = binding.desInput.text.toString().trim()
        val material = binding.materialInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val pickupDate = binding.pickupdateinput.text.toString().trim()
        val dropoffDate = binding.dropoffdateinput.text.toString().trim() // Added dropoff date
        val pickupTime = binding.pickuptimeInput.text.toString().trim()
        val dropoffTime = binding.droptimeInput.text.toString().trim()
        val length = binding.lengthInput.text.toString().trim()
        val weight = binding.weightInput.text.toString().trim()
        val limits = binding.limitsInput.text.toString().trim()
        val truckType = binding.TtypeInput.text.toString().trim()
        val price = binding.priceInput.text.toString().trim()

        // Validate required fields
        if (origin.isEmpty() || destination.isEmpty() || material.isEmpty() || phone.isEmpty()
            || pickupDate.isEmpty() || dropoffDate.isEmpty() || pickupTime.isEmpty() || dropoffTime.isEmpty()
            || length.isEmpty() || weight.isEmpty() || limits.isEmpty() || truckType.isEmpty() || price.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (!phone.matches(Regex("^[0-9]{10,15}$"))) {
            Toast.makeText(requireContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            return
        }


        // Check if limits input is either "full" or "partial"
        if (limits.lowercase() != "full" && limits.lowercase() != "partial") {
            Toast.makeText(requireContext(), "Equipment limits must be 'full' or 'partial'", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = Calendar.getInstance()

        // Parse pickup date
        val pickupDateParts = pickupDate.split("/")
        val pickupCalendar = Calendar.getInstance()
        pickupCalendar.set(pickupDateParts[2].toInt(), pickupDateParts[1].toInt() - 1, pickupDateParts[0].toInt())

        if (pickupCalendar.before(currentDate)) {
            Toast.makeText(requireContext(), "Pickup date cannot be in the past", Toast.LENGTH_SHORT).show()
            return
        }

        // Parse dropoff date
        val dropoffDateParts = dropoffDate.split("/")
        val dropoffCalendar = Calendar.getInstance()
        dropoffCalendar.set(dropoffDateParts[2].toInt(), dropoffDateParts[1].toInt() - 1, dropoffDateParts[0].toInt())

        if (dropoffCalendar.before(pickupCalendar)) {
            Toast.makeText(requireContext(), "Dropoff date cannot be before pickup date", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate weight and length
        val weightValue = weight.toDoubleOrNull()
        if (weightValue == null || weightValue <= 0) {
            Toast.makeText(requireContext(), "Weight must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        val lengthValue = length.toDoubleOrNull()
        if (lengthValue == null || lengthValue <= 0) {
            Toast.makeText(requireContext(), "Length must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate price
        val priceValue = price.toDoubleOrNull()
        if (priceValue == null || priceValue <= 0) {
            Toast.makeText(requireContext(), "Price must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }





        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress bar before starting data submission
        binding.progressBar.visibility = View.VISIBLE
        binding.nextButton.isEnabled = false

        // Create a map for load details, including dropoff date and price
        val loadDetails = mapOf(
            "origin" to origin,
            "destination" to destination,
            "material" to material,
            "phone" to phone,
            "pickupDate" to pickupDate,
            "dropoffDate" to dropoffDate, // Added dropoff date to Firebase
            "pickupTime" to pickupTime,
            "dropoffTime" to dropoffTime,
            "length" to length,
            "weight" to weight,
            "limits" to limits,
            "truckType" to truckType,
            "price" to price
        )

        // Save the new load details under the user's ID, adding to previous loads if any
        val loadRef = databaseReference.child(userId).child("loaddetails")
        loadRef.push().setValue(loadDetails)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data saved successfully!", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.nextButton.isEnabled = true
                navigateToFragment(postloads())
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.nextButton.isEnabled = true
            }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // Adds the transaction to the back stack
        transaction.commit()
    }


}
