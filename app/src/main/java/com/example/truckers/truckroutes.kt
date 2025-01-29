package com.example.truckers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar


class truckroutes : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var originInput: TextInputEditText
    private lateinit var destinationInput: TextInputEditText
    private lateinit var startDate: TextInputEditText
    private lateinit var endDate: TextInputEditText
    private var savedOrigin: String? = null
    private var savedDestination: String? = null
    private var savedStartDate: String? = null
    private var savedEndDate: String? = null
    private var routeId: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_truckroutes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the views
        originInput = view.findViewById(R.id.orign_input)
        destinationInput = view.findViewById(R.id.des_input)
        startDate = view.findViewById(R.id.sdateinput)
        endDate = view.findViewById(R.id.edate_input)
        val nextButton: Button = view.findViewById(R.id.next_button)
        progressBar = view.findViewById(R.id.progress_bar)
        sharedPreferences =  requireContext().getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
        // Setup date pickers for start and end date
        setupEditTextWithIcon(startDate) {
            showDatePicker { date -> startDate.setText(date) }
        }

        setupEditTextWithIcon(endDate) {
            showDatePicker { date -> endDate.setText(date) }
        }

        // Set click listener for the "Next" button
        nextButton.setOnClickListener {
            val origin = originInput.text.toString().trim()
            val destination = destinationInput.text.toString().trim()
            val start = startDate.text.toString().trim()
            val end = endDate.text.toString().trim()

            if (validateInputs(origin, destination, start, end)) {
                if (isDataModified(origin, destination, start, end)) {
                    saveDataToFirebase(origin, destination, start, end)
                    sharedPreferences.edit()

                        .putBoolean("routesdetailsComplete", true)
                        .apply()
                } else {
                    showToast("Data is unchanged, no need to save.")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Load data from Firebase when the fragment is resumed
        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("users")


        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val data = snapshot.value as? Map<String, String>
                data?.let {
                    // Store the saved data for comparison
                    savedOrigin = it["origin"]
                    savedDestination = it["destination"]
                    savedStartDate = it["startDate"]
                    savedEndDate = it["endDate"]

                    // Populate the fields with the saved data
                    originInput.setText(savedOrigin)
                    destinationInput.setText(savedDestination)
                    startDate.setText(savedStartDate)
                    endDate.setText(savedEndDate)
                }
            }
        }.addOnFailureListener {
            showToast("Failed to load route data.")
        }
    }

    private fun isDataModified(origin: String, destination: String, start: String, end: String): Boolean {
        // Check if the current data is different from the saved data
        return origin != savedOrigin || destination != savedDestination || start != savedStartDate || end != savedEndDate
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null) // Adds the transaction to the back stack
        transaction.commit()
    }

    private fun validateInputs(origin: String, destination: String, start: String, end: String): Boolean {
        if (origin.isEmpty()) {
            showToast("Origin is required")
            return false
        }
        if (destination.isEmpty()) {
            showToast("Destination is required")
            return false
        }
        if (start.isEmpty()) {
            showToast("Start date is required")
            return false
        }
        if (end.isEmpty()) {
            showToast("End date is required")
            return false
        }
        return true
    }
    private fun saveDataToFirebase(origin: String, destination: String, start: String, end: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid // Get the UID of the current user

            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")

            // Reference the user's node by UID
            val userRef = usersRef.child(userId)

            // Get the truck ID(s) associated with the current user
            val trucksRef = userRef.child("trucks")

            trucksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Assuming the user has only one truck, you can get the first truck's ID
                        val truckId = snapshot.children.first().key // Get the first truck's ID

                        if (truckId != null) {
                            val truckRef = userRef.child("trucks").child(truckId)

                            val routeData = mapOf(
                                "origin" to origin,
                                "destination" to destination,
                                "startDate" to start,
                                "endDate" to end
                            )

                            // Show the progress bar
                            progressBar.visibility = View.VISIBLE

                            // Directly update the truck node with the route data
                            truckRef.updateChildren(routeData).addOnCompleteListener { task ->
                                // Hide the progress bar
                                progressBar.visibility = View.GONE

                                if (task.isSuccessful) {
                                    showToast("Route saved successfully under truck ID $truckId")
                                    val sharedPreferences = requireContext().getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
                                    sharedPreferences.edit().putBoolean("routesdetailsComplete", true).apply()
                                    navigateToFragment(truckdetails())
                                } else {
                                    showToast("Failed to save route: ${task.exception?.message}")
                                }
                            }
                        } else {
                            showToast("No truck found for the current user.")
                        }
                    } else {
                        showToast("No trucks available for the current user.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Failed to retrieve truck data: ${error.message}")
                }
            })
        } else {
            showToast("No user is logged in.")
        }
    }






    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isDrawableEndClicked(editText: TextInputEditText, event: MotionEvent): Boolean {
        val drawableEnd = editText.compoundDrawablesRelative[2] ?: return false
        val drawableWidth = drawableEnd.bounds.width()
        return event.rawX >= (editText.right - drawableWidth - editText.paddingEnd)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEditTextWithIcon(editText: TextInputEditText, onClick: () -> Unit) {
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && isDrawableEndClicked(editText, event)) {
                onClick()
                editText.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}

