package com.example.truckers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class TrucRoutes : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_truc_routes)

        val originInput: TextInputEditText = findViewById(R.id.orign_input)
        val destinationInput: TextInputEditText = findViewById(R.id.des_input)
        val startDate: TextInputEditText = findViewById(R.id.sdateinput)
        val endDate: TextInputEditText = findViewById(R.id.edate_input)
        val nextButton: Button = findViewById(R.id.next_button)
        progressBar = findViewById(R.id.progress_bar)

        setupEditTextWithIcon(startDate) {
            showDatePicker { date -> startDate.setText(date) }
        }

        setupEditTextWithIcon(endDate) {
            showDatePicker { date -> endDate.setText(date) }
        }

        nextButton.setOnClickListener {
            val origin = originInput.text.toString().trim()
            val destination = destinationInput.text.toString().trim()
            val start = startDate.text.toString().trim()
            val end = endDate.text.toString().trim()

            if (validateInputs(origin, destination, start, end)) {
                saveDataToFirebase(origin, destination, start, end)
            }
        }
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
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("TruckRoutes")

        val routeId = ref.push().key ?: return
        val routeData = mapOf(
            "routeId" to routeId,
            "origin" to origin,
            "destination" to destination,
            "startDate" to start,
            "endDate" to end
        )

        // Show the progress bar
        progressBar.visibility = View.VISIBLE

        ref.child(routeId).setValue(routeData).addOnCompleteListener { task ->
            // Hide the progress bar
            progressBar.visibility = View.GONE

            if (task.isSuccessful) {
                showToast("Route saved successfully")
                val sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("routesdetailsCompleted", true).apply()
                val intent = Intent(this, vehicleinfo::class.java)
                startActivity(intent)
            } else {
                showToast("Failed to save route: ${task.exception?.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
            this,
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
