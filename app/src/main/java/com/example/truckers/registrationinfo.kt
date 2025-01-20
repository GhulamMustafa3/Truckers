package com.example.truckers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class registrationinfo : AppCompatActivity() {
    private lateinit var plateNo: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrationinfo)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)

        plateNo = findViewById(R.id.registration_plate_input)
        val nextBtn: Button = findViewById(R.id.next_button)

        nextBtn.setOnClickListener {
            if (plateNo.text.toString().isNotEmpty()) {
                // Set the flag for registration info as completed
                sharedPreferences.edit().putBoolean("registrationInfoCompleted", true).apply()
                val sharedPreferences = getSharedPreferences("VehicleInfoFlags", MODE_PRIVATE)



                // Navigate to the next activity
                val intent = Intent(this, vehiclephoto::class.java)
                startActivity(intent)
            } else {
                plateNo.error = "Please enter the plate number"
            }
        }
    }
}
