package com.example.truckers
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var orign:TextView
    private lateinit var dest:TextView
    private lateinit var material:TextView
    private lateinit var phone:TextView
    private lateinit var pickupdate:TextView
    private lateinit var dropoffdate:TextView
    private lateinit var pickuptime:TextView
    private lateinit var dropofftime:TextView
    private lateinit var length:TextView
    private lateinit var weight:TextView
    private lateinit var equiplimit:TextView
    private lateinit var trucktype:TextView
    private lateinit var price:TextView
    private lateinit var nextbtn:Button
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_loaddetailsform, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orign=view.findViewById(R.id.orign_input)
        dest=view.findViewById(R.id.des_input)
        material=view.findViewById(R.id.material_input)
        phone=view.findViewById(R.id.phone_input)
        pickupdate=view.findViewById(R.id.pickupdateinput)
        dropoffdate=view.findViewById(R.id.dropoffdateinput)
        pickuptime=view.findViewById(R.id.pickuptime_input)
        dropofftime=view.findViewById(R.id.droptime_input)
        length=view.findViewById(R.id.length_input)
        weight=view.findViewById(R.id.weight_input)
        equiplimit=view.findViewById(R.id.limits_input)
        trucktype=view.findViewById(R.id.Ttype_input)
        price=view.findViewById(R.id.priceInput)
        nextbtn=view.findViewById(R.id.next_button)
        progressBar=view.findViewById(R.id.progressBar)
        pickupdate.setOnClickListener { showDatePicker() }
       dropoffdate.setOnClickListener{showDatePicker2()}
        pickuptime.setOnClickListener { showTimePicker(pickuptime) }
        dropofftime.setOnClickListener { showTimePicker(dropofftime) }
        nextbtn.setOnClickListener { validateAndSubmit() }


    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                pickupdate.setText(selectedDate)
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
                dropoffdate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker(targetTextView: TextView) {
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val amPm = if (hourOfDay < 12) "AM" else "PM"
                val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm)
                targetTextView.text = formattedTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePicker.show()
    }



    private fun validateAndSubmit() {
        val origin = orign.text.toString().trim()
        val destination = dest.text.toString().trim()
        val material = material.text.toString().trim()
        val phone = phone.text.toString().trim()
        val pickupDate = pickupdate.text.toString().trim()
        val dropoffDate = dropoffdate.text.toString().trim()
        val pickupTime = pickuptime.text.toString().trim()
        val dropoffTime = dropofftime.text.toString().trim()
        val length = length.text.toString().trim()
        val weight = weight.text.toString().trim()
        val limits = equiplimit.text.toString().trim()
        val truckType = trucktype.text.toString().trim()
        val price = price.text.toString().trim()

        if (origin.isEmpty() || destination.isEmpty() || material.isEmpty() || phone.isEmpty()
            || pickupDate.isEmpty() || dropoffDate.isEmpty() || pickupTime.isEmpty() || dropoffTime.isEmpty()
            || length.isEmpty() || weight.isEmpty() || limits.isEmpty() || truckType.isEmpty() || price.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!phone.matches(Regex("^[0-9]{10,15}$"))) {
            Toast.makeText(requireContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (limits.lowercase() != "full" && limits.lowercase() != "partial") {
            Toast.makeText(requireContext(), "Equipment limits must be 'full' or 'partial'", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = Calendar.getInstance()

        val pickupDateParts = pickupDate.split("/")
        val pickupCalendar = Calendar.getInstance()
        pickupCalendar.set(pickupDateParts[2].toInt(), pickupDateParts[1].toInt() - 1, pickupDateParts[0].toInt())

        if (pickupCalendar.before(currentDate)) {
            Toast.makeText(requireContext(), "Pickup date cannot be in the past", Toast.LENGTH_SHORT).show()
            return
        }

        val dropoffDateParts = dropoffDate.split("/")
        val dropoffCalendar = Calendar.getInstance()
        dropoffCalendar.set(dropoffDateParts[2].toInt(), dropoffDateParts[1].toInt() - 1, dropoffDateParts[0].toInt())

        if (dropoffCalendar.before(pickupCalendar)) {
            Toast.makeText(requireContext(), "Dropoff date cannot be before pickup date", Toast.LENGTH_SHORT).show()
            return
        }

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

        // Show progress bar and disable the button

        nextbtn.isEnabled = false

        val loadDetails = mapOf(
            "origin" to origin,
            "destination" to destination,
            "material" to material,
            "phone" to phone,
            "pickupDate" to pickupDate,
            "dropoffDate" to dropoffDate,
            "pickupTime" to pickupTime,
            "dropoffTime" to dropoffTime,
            "length" to length,
            "weight" to weight,
            "limits" to limits,
            "truckType" to truckType,
            "price" to price
        )

        val loadRef = databaseReference.child(userId).child("loaddetails")
        loadRef.push().setValue(loadDetails)
            .addOnSuccessListener {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Data saved successfully!", Toast.LENGTH_SHORT).show()

                    nextbtn.isEnabled = true
                    navigateToFragment(postloads())
                }
            }
            .addOnFailureListener {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()

                    nextbtn.isEnabled = true
                }
            }
    }




    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // Adds the transaction to the back stack
        transaction.commit()
    }


}
