package com.example.truckers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IndividualDriverRegis : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_individual_driver_regis)
        sharedPreferences = getSharedPreferences("DriverRegistrationPrefs", MODE_PRIVATE)
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
            if (isAllInfoComplete()) {
                Toast.makeText(this, "Registration Completed!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                // Proceed to the next step
            } else {
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
