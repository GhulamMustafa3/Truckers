package com.example.truckers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class TrucRoutes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_truc_routes)

        val origin: TextInputEditText = findViewById(R.id.orign_input)
        val destination: TextInputEditText = findViewById(R.id.des_input)
        val startDate: TextInputEditText = findViewById(R.id.sdateinput)
        val endDate: TextInputEditText = findViewById(R.id.edate_input)
        val nextButton: Button = findViewById(R.id.next_button)

        // Setup listeners for end-icon clicks
        setupEditTextWithIcon(origin) {
            openMaps(origin.text.toString()) // Opens map for origin
        }

        setupEditTextWithIcon(destination) {
            openMaps(destination.text.toString()) // Opens map for destination
        }

        setupEditTextWithIcon(startDate) {
            showDatePicker { date -> startDate.setText(date) } // Opens date picker for start date
        }

        setupEditTextWithIcon(endDate) {
            showDatePicker { date -> endDate.setText(date) } // Opens date picker for end date
        }

        nextButton.setOnClickListener {
            // Navigate to the next screen
            val intent = Intent(this, vehicleinfo::class.java)
            startActivity(intent)

            // Example: Set shared preference flag (optional)
            val sharedPreferences = getSharedPreferences("TruckRoutesPrefs", MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("routesDetailsCompleted", true).apply()
        }
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
                editText.performClick() // Ensures compatibility with accessibility tools
                true
            } else {
                false
            }
        }

        // Override performClick for accessibility
        editText.setOnClickListener {
            // Optional: Handle normal clicks if needed
        }
    }

    private fun openMaps(location: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=$location")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Fallback: Open any browser if Google Maps is not installed
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$location"))
            startActivity(webIntent)
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
