package com.example.truckers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class IndividualDriverRegis : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_individual_driver_regis)

        sharedPreferences = getSharedPreferences("DriverRegistrationPrefs", MODE_PRIVATE)
        progressBar = findViewById(R.id.progress_bar)

        val Basicinfo: TextView = findViewById(R.id.basic_info)
        val Cnic: TextView = findViewById(R.id.cnic_info)
        val vehiclinfo: TextView = findViewById(R.id.vehicle_info)
        val btndone: Button = findViewById(R.id.btn_done)

        Basicinfo.setOnClickListener {
            val intent = Intent(this, BasicInfo::class.java)
            startActivity(intent)
        }

        Cnic.setOnClickListener {
            val intent = Intent(this, CNIC::class.java)
            startActivity(intent)
        }

        vehiclinfo.setOnClickListener {
            val intent = Intent(this, vehicleinfo::class.java)
            startActivity(intent)
        }

        btndone.setOnClickListener {
            // Show the ProgressBar while checking the completion status
            progressBar.visibility = View.VISIBLE

            if (isAllInfoComplete()) {
                // Hide ProgressBar and proceed to the next step
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Registration Completed!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                // Hide ProgressBar and show an error message
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Please fill all required information!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun isAllInfoComplete(): Boolean {
        return sharedPreferences.getBoolean("isBasicInfoComplete", false) &&
                sharedPreferences.getBoolean("isCnicInfoComplete", false) &&
                sharedPreferences.getBoolean("isVehicleInfoComplete", false)
    }
}
